"use client"


import {Input} from "@/components/ui/input"
import {Button} from "@/components/ui/button"
import React, {useState} from "react";
import axios, {AxiosError} from 'axios';
import {useRouter} from "next/navigation";
import {toast} from "sonner";

interface ErrorResponse {
    message?: string;
}

export default function Joingame() {

    const router = useRouter();

    let user : string | null = null;
    let username : string | null = null;
    let bearer : string | null = null;

    if (typeof window !== "undefined") {
        user = localStorage.getItem("user")
        username = localStorage.getItem("username")
        bearer = localStorage.getItem("bearer_token")
    }
    console.log(user)

    const [nickname, setNickname] = useState('')
    const [gameId, setGameId] = useState('')


    const handleJoinGameButtonGuest = async (event: React.SyntheticEvent<HTMLElement>) => {
        event.preventDefault()
        try {
            const response = await axios.post('http://localhost:8080/registration/registerGuest', {username: nickname}
            )


            if (typeof window !== "undefined") {
                localStorage.setItem("username", response.data.nickname)
                localStorage.setItem("bearer_token", response.data.bearer_token)


            }

            const connectGameResponse = await axios.post('http://localhost:8080/game/connect', {
                    player: {
                        username: response.data.nickname
                    }, gameId: gameId

                }, {
                    headers: {
                        Authorization: "Bearer " + response.data.bearer_token
                    }
                }
            )

            if (connectGameResponse.status == 200) {

                let ticOrToe = connectGameResponse.data.player2Mark === "X" ? "tic" : "toe";

                router.push(`/tictactoe?gameId=${gameId}&userMark=${ticOrToe}`)
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
    const handleJoinGameButtonUser = async (event: React.SyntheticEvent<HTMLElement>) => {
        event.preventDefault()
        try {


            const connectGameResponse = await axios.post('http://localhost:8080/game/connect', {
                    player: {
                        username: username
                    }, gameId: gameId

                }, {
                    headers: {
                        Authorization: "Bearer " + bearer
                    }
                }
            )

            if (connectGameResponse.status == 200) {

                let ticOrToe = connectGameResponse.data.player2Mark === "X" ? "tic" : "toe";

                router.push(`/tictactoe?gameId=${gameId}&userMark=${ticOrToe}`)
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
        <>
            {user === "false" ? (
                <div className="flex items-center justify-center min-h-screen bg-purple-600">
                    <div className="w-full max-w-md p-8 bg-white rounded-lg shadow-lg">
                        <div className="text-center flex items-center justify-center">
                            <UserIcon className="w-6 h-6 mr-2"/>
                            <h2 className="text-2xl font-bold">Hello Guest</h2>
                        </div>
                        <p className="text-muted-foreground text-center">Enter nickname and Game ID to play</p>
                        <div className="mt-6 space-y-4">
                            <div className="relative">
                                <UserIcon className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground"/>
                                <Input id="nickname" type="text" placeholder="Nickname" className="pl-10"
                                       value={nickname} onChange={(e) => setNickname(e.target.value)}/>
                            </div>
                            <div className="relative">
                                <GamepadIcon
                                    className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground"/>
                                <Input id="gameId" type="text" placeholder="Game ID" className="pl-10" value={gameId}
                                       onChange={(e) => setGameId(e.target.value)}/>
                            </div>
                            <Button
                                type="submit"
                                className="w-full rounded-lg bg-[#2ecc71] py-3 px-6 font-bold text-white hover:bg-[#27ae60] focus:outline-none focus:ring-2 focus:ring-[#2ecc71] focus:ring-offset-2"
                                onClick={handleJoinGameButtonGuest}>
                                Join Game
                            </Button>
                        </div>
                    </div>
                </div>
            ) : user === "true" ? (
                <div className="flex items-center justify-center min-h-screen bg-purple-600">
                    <div className="w-full max-w-md p-8 bg-white rounded-lg shadow-lg">
                        <div className="text-center flex items-center justify-center">
                            <UserIcon className="w-6 h-6 mr-2"/>
                            <h2 className="text-2xl font-bold">Hello {username}</h2>
                        </div>
                        <p className="text-muted-foreground text-center">Enter Game ID to play</p>
                        <div className="mt-6 space-y-4">
                            <div className="relative">
                                <GamepadIcon
                                    className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground"/>
                                <Input id="gameId" type="text" placeholder="Game ID" className="pl-10" value={gameId}
                                       onChange={(e) => setGameId(e.target.value)}/>
                            </div>
                            <Button
                                type="submit"
                                className="w-full rounded-lg bg-[#2ecc71] py-3 px-6 font-bold text-white hover:bg-[#27ae60] focus:outline-none focus:ring-2 focus:ring-[#2ecc71] focus:ring-offset-2"
                                onClick={handleJoinGameButtonUser}>
                                Join Game
                            </Button>
                        </div>
                    </div>
                </div>
            ) : null}
        </>
    )
}

function GamepadIcon(props:any) {
    return (
        <svg
            {...props}
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            <line x1="6" x2="10" y1="12" y2="12"/>
            <line x1="8" x2="8" y1="10" y2="14"/>
            <line x1="15" x2="15.01" y1="13" y2="13"/>
            <line x1="18" x2="18.01" y1="11" y2="11"/>
            <rect width="20" height="12" x="2" y="6" rx="2"/>
        </svg>
    )
}


function UserIcon(props:any) {
    return (
        <svg
            {...props}
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/>
            <circle cx="12" cy="7" r="4"/>
        </svg>
    )
}
