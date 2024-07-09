"use client";

import {cn} from "@/lib/utils";
import React, {useState, useEffect} from "react";
import {useRouter, useSearchParams} from "next/navigation";
import {Stomp} from "@stomp/stompjs";
import SockJS from 'sockjs-client';
import axios, {AxiosError} from 'axios';
import {toast, Toaster} from "sonner";

interface ErrorResponse {
    message?: string;
}

interface Message {
    senderMark: string;
    message: string;
}

export default function Tictactoe() {

    const router = useRouter();
    const searchParams = useSearchParams();
    const gameId = (searchParams.get('gameId') as string)
    const userMark = (searchParams.get('userMark') as string)
    const userMarkSymbol = userMark === "tic" ? "X" : "O"

    let usernameFromLocalStorage: string | null = null;
    let bearer_token: string | null = null;
    let isUser: string | null = null;

    if (typeof window !== "undefined") {
        usernameFromLocalStorage = localStorage.getItem("username")
        bearer_token = localStorage.getItem("bearer_token")
        isUser = localStorage.getItem("user")
    }

    const [board, setBoard] = useState(Array(3).fill(null).map(() => Array(3).fill(null)));

    const [messages, setMessages] = useState<Message[]>([]);
    const [turn, setTurn] = useState("X");

    const [firstPlayerUsername, setFirstPlayerUsername] = useState(null);

    const [secondPlayerUsername, setSecondPlayerUsername] = useState(null);

    const [requestSent, setRequestSent] = useState(false);

    const [winner, setWinner] = useState(null);
    const [tie, setTie] = useState(false);


    const [newMessageText, setNewMessageText] = useState("");

    useEffect(() => {


        const sendRequest = async () => {
            try {
                let response = await axios.post('http://localhost:8080/game/info',
                    {gameId: gameId}
                    , {
                        headers: {
                            Authorization: "Bearer " + bearer_token
                        }
                    });
                console.log("done")

                setRequestSent(true); // Prevent further requests
            } catch (error) {
                console.error(error);
            }
        };

        sendRequest();

        let socketFactory = () => new SockJS("http://localhost:8080/gameplay");

        let stompClient = Stomp.over(socketFactory);
        stompClient.connect({}, function (frame: any) {

            stompClient.subscribe("/topic/game-progress/" + gameId, function (reponse) {

                let data = JSON.parse(reponse.body);
                console.log(data)
                let wsBoard = data.board;
                let newBoard = wsBoard.map((row: any) => row.map((cell: any) => cell === 1 ? "X" : cell === 2 ? "O" : null));
                setBoard(newBoard)
                setFirstPlayerUsername(data.player1.username)
                setSecondPlayerUsername(data.player2.username)
                setTurn(data.turn)
                setWinner(data.winner)
                setTie(data.tie)


            })
            stompClient.subscribe("/topic/game-chat/" + gameId, function (reponse) {

                let data = JSON.parse(reponse.body);
                console.log(data)
                setMessages(currentMessages => {

                    const messageExists = currentMessages.some(message =>
                        message.senderMark === data.senderMark && message.message === data.message
                    );

                    if (!messageExists) {
                        return [...currentMessages, data];
                    }
                    return currentMessages;
                });


            })
        }, function (error: any) {
            console.error("Connection error:", error);

        });
    }, [requestSent, bearer_token, gameId]);


    const handleClick = async (flatIndex: number) => {
        if (userMarkSymbol != turn) {
            toast.error("It's not your turn!")
            return
        }


        const row = Math.floor(flatIndex / 3);
        const col = flatIndex % 3;

        if (board[row][col] === null) {
            try {
                const response = await axios.post('http://localhost:8080/game/makeMove', {
                    playerMark: userMarkSymbol,
                    coordinateX: row,
                    coordinateY: col,
                    gameId: gameId
                }, {
                    headers: {
                        Authorization: "Bearer " + bearer_token
                    }
                });


            } catch (error) {
                console.log(error);
            }
        } else if (board[row][col] != null) {
            toast.error("There's already mark in this cell")
        }
    };

    const handleSendMessage = async (event: React.SyntheticEvent<HTMLElement>) => {
        event.preventDefault()

        try {
            const response = await axios.post("http://localhost:8080/chat/sendMessage", {
                gameId: gameId,
                senderMark: userMarkSymbol,
                message: newMessageText
            }, {
                headers: {
                    Authorization: "Bearer " + bearer_token
                }
            })
            setNewMessageText("");

        } catch (error) {
            const axiosError = error as AxiosError<ErrorResponse>;

            if (axiosError.response) {
                let errorMessage = axiosError.response.data.message ? axiosError.response.data.message : JSON.stringify(axiosError.response.data);
                if (!axiosError.response.data.message) {
                    errorMessage = errorMessage.slice(1, -1);
                }
                toast.error(errorMessage);
            }


        }

    }

    const handleRedirectToAccount = async (event: React.SyntheticEvent<HTMLElement>) => {
        event.preventDefault()

        router.push("/account")
    }

    const handleRedirectToCreateAccount = async (event: React.SyntheticEvent<HTMLElement>) => {
        event.preventDefault()
        try {
            const response = await axios.post("http://localhost:8080/registration/deleteGuest", {
                nickname: usernameFromLocalStorage,
                bearer_token: bearer_token
            }, {
                headers: {
                    Authorization: "Bearer " + bearer_token
                }
            })

            if (response.status == 200) {
                if (typeof window !== "undefined") {

                    localStorage.removeItem("bearer_token")
                    localStorage.removeItem("user")
                    localStorage.removeItem("username")

                }

                router.push("/")
            }


        } catch (error) {
            const axiosError = error as AxiosError<ErrorResponse>;

            if (axiosError.response) {
                let errorMessage = axiosError.response.data.message ? axiosError.response.data.message : JSON.stringify(axiosError.response.data);
                if (!axiosError.response.data.message) {
                    errorMessage = errorMessage.slice(1, -1);
                }
                toast.error(errorMessage);
            }


        }

    }

    return (

        <div className="flex flex-col min-h-screen bg-purple-600">
            <Toaster richColors/>
            <header className="bg-purple-700 text-white p-4 flex justify-between items-center shadow-sm">
                <h1 className="text-2xl font-bold">Tic Tac Toe</h1>

                {
                    userMark === "tic" ? (
                        <div className="flex items-center gap-4">
                            <div
                                className="bg-green-500 rounded-full w-8 h-8 flex items-center justify-center text-white font-bold">
                                X
                            </div>

                            {secondPlayerUsername ? (
                                <div
                                    className="bg-red-500 rounded-full w-8 h-8 flex items-center justify-center text-white font-bold">
                                    O
                                </div>


                            ) : (

                                <div
                                    className="bg-black opacity-25 animate-pulse rounded-full w-8 h-8 flex items-center justify-center text-white font-bold">
                                    ?
                                </div>
                            )}
                        </div>
                    ) : userMark === "toe" ? (
                        secondPlayerUsername ? (
                            <div className="flex items-center gap-4">
                                <div
                                    className="bg-green-500 rounded-full w-8 h-8 flex items-center justify-center text-white font-bold">
                                    X
                                </div>
                                <div
                                    className="bg-red-500 rounded-full w-8 h-8 flex items-center justify-center text-white font-bold">
                                    O
                                </div>

                            </div>
                        ) : (
                            <div className="flex items-center gap-4">
                                <div
                                    className="bg-black opacity-25 animate-pulse rounded-full w-8 h-8 flex items-center justify-center text-white font-bold">
                                    ?
                                </div>
                                <div
                                    className="bg-red-500 rounded-full w-8 h-8 flex items-center justify-center text-white font-bold">
                                    O
                                </div>
                            </div>
                        )
                    ) : null
                }
            </header>
            <div className="flex-1 flex">
                <div className="flex items-center justify-center w-full flex-col">
                    {winner === "X" || winner === "O" ? (
                        <h1 className="text-4xl mb-8 font-bold text-white drop-shadow-lg flex items-center justify-center">
                            Game finished winner :
                            <div
                                className={`rounded-full w-10 h-10 flex items-center justify-center text-base text-white font-bold ml-4 ${
                                    winner === "X" ? "bg-green-500" : "bg-red-500"
                                }`}
                            >
                                {winner}
                            </div>
                        </h1>
                    ) : tie === true ? (
                        <h1 className="text-4xl mb-8 font-bold text-white drop-shadow-lg flex items-center justify-center">
                            {"It's a tie"}
                        </h1>
                    ) : null}


                    <div className="grid grid-cols-3 gap-4 mx-auto">
                        {board.map((row, rowIndex) =>
                            row.map((value, colIndex) => {
                                const flatIndex = rowIndex * 3 + colIndex;
                                return (
                                    <div
                                        key={flatIndex}
                                        className={cn(
                                            "bg-white rounded-md w-32 h-32 flex items-center justify-center text-6xl font-bold text-purple-600 transition-colors duration-200 cursor-not-allowed",
                                            value === null && "cursor-pointer hover:bg-purple-100"
                                        )}
                                        onClick={() => handleClick(flatIndex)}
                                    >
                                        {value === "X" ? (
                                            <div className="text-green-500">X</div>
                                        ) : value === "O" ? (
                                            <div className="text-red-500">O</div>
                                        ) : null}
                                    </div>
                                );
                            })
                        )}
                    </div>
                    {winner != null || tie === true ? (
                        <div className="flex gap-4 justify-center my-8">
                            {isUser === "true" ? (
                                <button
                                    className="bg-green-500 text-white rounded-md px-8 py-4 hover:bg-green-600 transition-colors duration-200"

                                    onClick={handleRedirectToAccount}>
                                    Account
                                </button>
                            ) : isUser === "false" ? (
                                <button
                                    className="bg-green-500 text-white rounded-md px-4 py-4 hover:bg-green-600 transition-colors duration-200"

                                    onClick={handleRedirectToCreateAccount}>
                                    Create account
                                </button>
                            ) : null}

                        </div>) : null}
                </div>
                <div className="bg-white rounded-lg shadow-lg p-4 my-bf2 w-160 h-full overflow-hidden">
                    <div className="flex justify-between items-center mb-4">
                        <div className="text-sm font-medium text-black/50">Game Id: {gameId}</div>

                    </div>

                    {
                        userMark === "tic" ? (
                            <div>
                                <div className="flex items-center gap-2 mb-4">
                                    <div
                                        className="bg-green-500 rounded-full w-8 h-8 flex items-center justify-center text-white font-bold">X
                                    </div>
                                    <div className="font-medium">{usernameFromLocalStorage}</div>
                                </div>
                                {secondPlayerUsername ? (
                                    <div className="flex items-center gap-2 mb-4">
                                        <div
                                            className="bg-red-500 rounded-full w-8 h-8 flex items-center justify-center text-white font-bold">O
                                        </div>
                                        <div
                                            className="font-medium">  {secondPlayerUsername === usernameFromLocalStorage ? firstPlayerUsername : secondPlayerUsername}</div>
                                    </div>
                                ) : (
                                    <div className="flex items-center gap-2 mb-4 animate-pulse">
                                        <div
                                            className="bg-black rounded-full w-8 h-8 flex items-center justify-center text-white font-bold opacity-25">?
                                        </div>
                                        <div className="font-medium">Waiting for player...</div>
                                    </div>
                                )}
                            </div>
                        ) : userMark === "toe" ? (
                            <div>
                                {secondPlayerUsername ? (
                                    <>
                                        <div className="flex items-center gap-2 mb-4">
                                            <div
                                                className="bg-green-500 rounded-full w-8 h-8 flex items-center justify-center text-white font-bold">X
                                            </div>
                                            <div
                                                className="font-medium">{firstPlayerUsername === usernameFromLocalStorage ? secondPlayerUsername : firstPlayerUsername}</div>
                                        </div>
                                        <div className="flex items-center gap-2 mb-4">
                                            <div
                                                className="bg-red-500 rounded-full w-8 h-8 flex items-center justify-center text-white font-bold">O
                                            </div>
                                            <div className="font-medium">{usernameFromLocalStorage}</div>
                                        </div>
                                    </>
                                ) : (
                                    <>
                                        <div className="flex items-center gap-2 mb-4 animate-pulse">
                                            <div
                                                className="bg-black rounded-full w-8 h-8 flex items-center justify-center text-white font-bold opacity-25">?
                                            </div>
                                            <div className="font-medium">Waiting for player...</div>
                                        </div>
                                        <div className="flex items-center gap-2 mb-4">
                                            <div
                                                className="bg-red-500 rounded-full w-8 h-8 flex items-center justify-center text-white font-bold">O
                                            </div>
                                            <div className="font-medium">{usernameFromLocalStorage}</div>
                                        </div>
                                    </>
                                )}
                            </div>
                        ) : null
                    }


                    <div className="border-t pt-4 h-64 overflow-y-auto">
                        <div className="flex flex-col gap-2">
                            {messages.map((message, index) => (
                                <div key={index} className="flex items-center gap-2">
                                    <div
                                        className={`rounded-full w-8 h-8 flex items-center justify-center text-white font-bold ${
                                            message.senderMark === "X" ? "bg-green-500" : "bg-red-500"
                                        }`}
                                    >
                                        {message.senderMark}
                                    </div>
                                    <div>{message.message}</div>
                                </div>
                            ))}
                        </div>
                    </div>
                    <div className="border-t pt-4">
                        <div className="flex justify-between items-center mb-2">
                            {turn != null && (
                                <div className="flex items-center gap-2">
                                    <span>Turn:</span>
                                    <div
                                        className={`rounded-full w-6 h-6 flex items-center justify-center text-white font-bold ${
                                            turn === "X" ? "bg-green-500" : "bg-red-500"
                                        }`}
                                    >
                                        {turn}
                                    </div>
                                </div>
                            )}

                        </div>
                        <textarea
                            className="w-full bg-purple-100 rounded-md p-2 mt-2 focus:outline-none focus:ring-2 focus:ring-purple-600"
                            placeholder="Type your message..."
                            value={newMessageText}
                            onChange={(e) => setNewMessageText(e.target.value)}
                        />
                        <button
                            className="w-full rounded-lg bg-purple-600 py-3 px-6 font-bold text-white hover:bg-[#5b44e0] focus:outline-none focus:ring-2 focus:ring-[#6c5ce7] focus:ring-offset-2"
                            onClick={handleSendMessage}
                        >
                            Send
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
