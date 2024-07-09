"use client"

import React, {useState, useEffect} from "react"
import {Button} from "@/components/ui/button"
import axios, {AxiosError} from 'axios';
import {useRouter} from "next/navigation";
import {Toaster, toast} from "sonner";

interface ErrorResponse {
    message?: string;
}

export default function Account() {

    const router = useRouter();

    const [isButtonDisabled, setIsButtonDisabled] = useState(() => {
        if (typeof window !== 'undefined') {
            return localStorage.getItem('2FAButtonDisabled') === 'true';
        }
        return false;
    });


    const [username, setUsername] = useState(() => {
        if (typeof window !== 'undefined') {
            return localStorage.getItem('username');
        }
        return "User"

    });

    const handleEnable2FAButton = async (event: React.SyntheticEvent<HTMLElement>) => {
        event.preventDefault()

        try {
            let bearer;
            if (typeof window !== "undefined") {

                bearer = localStorage.getItem("bearer_token")
            }

            const response = await axios.get("http://localhost:8080/enable2FA", {
                headers: {
                    Authorization: `Bearer ${bearer}`
                }
            })

            if (response.status == 200) {

                const qrCode = response.data
                toast.success(qrCode)
                router.push(`/setup2fa?qrCode=${qrCode}`)


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
    const handleCreateGameButton = async (event: React.SyntheticEvent<HTMLElement>) => {
        event.preventDefault()

        try {
            let bearer;
            let username
            if (typeof window !== "undefined") {
                bearer = localStorage.getItem("bearer_token")
                username = localStorage.getItem("username")
            }

            const response = await axios.post("http://localhost:8080/game/create", {username: username}, {
                headers: {
                    Authorization: `Bearer ${bearer}`
                }
            })
            if (response.status == 200) {
                let gameId = response.data.gameId
                let ticOrToe = response.data.player1Mark === "X" ? "tic" : "toe";
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

    const handleJoinGameButton = () => {
        router.push("/joingame")
    }
    const handleLogoutButton = async (event: React.SyntheticEvent<HTMLElement>) => {
        event.preventDefault()

        try {
            let bearer;
            if (typeof window !== "undefined") {
                bearer = localStorage.getItem("bearer_token")
            }

            const response = await axios.post("http://localhost:8080/blacklistToken", {bearer_token: bearer}, {
                headers: {
                    Authorization: `Bearer ${bearer}`
                }
            })

            if (response.status == 200) {
                if (typeof window !== "undefined") {

                    localStorage.removeItem("bearer_token")
                    localStorage.removeItem("2FAButtonDisabled")
                    localStorage.removeItem("username")
                    localStorage.removeItem("user")

                }
                router.push(`/`)
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

        <div className="flex min-h-screen items-center justify-center bg-purple-600">
            <Toaster richColors/>
            <div className="w-full max-w-md space-y-8">

                <div className="rounded-lg border bg-[#f1f1f1] p-8 shadow-lg">
                    <div className="text-center">
                        <div className="flex items-center justify-center">
                            <UserIcon className="mr-2 h-6 w-6"/>
                            <h2 className="text-2xl font-bold">Hello {username}</h2>
                        </div>
                        <p className="text-muted-foreground">What are we doing today?</p>
                    </div>
                    <form className="mt-6 space-y-4">
                        {!isButtonDisabled && (
                            <Button
                                type="button"
                                className="w-full rounded-lg bg-[#6c5ce7] py-3 px-6 font-bold text-white hover:bg-[#5b44e0] focus:outline-none focus:ring-2 focus:ring-[#6c5ce7] focus:ring-offset-2"
                                onClick={handleEnable2FAButton}
                            >
                                Enable 2FA
                            </Button>)}
                        <Button
                            type="submit"

                            className="w-full rounded-lg bg-[#2ecc71] py-3 px-6 font-bold text-white hover:bg-[#27ae60] focus:outline-none focus:ring-2 focus:ring-[#2ecc71] focus:ring-offset-2"
                            onClick={handleCreateGameButton}>
                            Create Game
                        </Button>
                        <Button
                            type="button"
                            className="w-full rounded-lg bg-amber-500 py-3 px-6 font-bold text-white hover:bg-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:ring-offset-2"

                            onClick={handleJoinGameButton}
                        >
                            Join Game
                        </Button>
                        <Button
                            type="button"
                            className="w-full rounded-lg bg-[#e74c3c] py-3 px-6 font-bold text-white hover:bg-[#c0392b] focus:outline-none focus:ring-2 focus:ring-[#e74c3c] focus:ring-offset-2"
                            onClick={handleLogoutButton}>
                            Logout
                        </Button>
                    </form>
                </div>

            </div>
        </div>
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
