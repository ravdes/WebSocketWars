"use client"


import {Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter} from "@/components/ui/card"
import {Input} from "@/components/ui/input"
import {Button} from "@/components/ui/button"
import {useSearchParams} from 'next/navigation';
import {Toaster, toast} from "sonner";
import Image from "next/image";
import axios, {AxiosError} from 'axios';
import {useRouter} from "next/navigation";
import React, { useState } from "react"

interface ErrorResponse {
    message?: string;
}

export default function Setup2FA() {
    const router = useRouter();

    const searchParams = useSearchParams();
    const qrCode = (searchParams.get('qrCode') as string).replace(/\s/g, "+");


    const [code, setCode] = useState('')


    const handleVerifyButton = async (event: React.SyntheticEvent<HTMLElement>) => {
        event.preventDefault()
        try {
            let bearer;
            if (typeof window !== "undefined") {

                bearer = localStorage.getItem("bearer_token")
            }

            const response = await axios.post("http://localhost:8080/verify2FA", { code: code }, {
                headers: {
                    Authorization: `Bearer ${bearer}`
                }
            })

            if (response.status == 200) {


                if (typeof window !== "undefined") {
                    localStorage.setItem("2FAButtonDisabled", "true")
                }

                router.push(`/account`)



            }
        } catch (error) {
            const axiosError = error as AxiosError<ErrorResponse>;

            if (axiosError.response) {

                // @ts-ignore
                toast.error(axiosError.response.data);
            }

        }
    }
    return (
        <div className="flex items-center justify-center min-h-screen bg-purple-600">
            <Card className="w-full max-w-md p-2 bg-white">
                <CardHeader className="text-center">
                    <CardTitle className="text-xl font-bold">Set Up Two Factor Authentication</CardTitle>
                    <CardDescription>Scan QR code with your phone</CardDescription>
                </CardHeader>
                <CardContent className="flex flex-col items-center space-y-4">
                    <Image src={qrCode} width={192} height={192} alt="QR Code" className="w-48 h-48"/>
                    <div className="w-full space-y-4">
                        <Input id="qr-code" placeholder="Enter code" value = {code} onChange = {e => setCode(e.target.value)}/>
                        <Button className="bg-black text-white w-full" onClick = {handleVerifyButton}>Verify</Button>
                    </div>
                </CardContent>
                <CardFooter className="py-0"/>
            </Card>
        </div>
    )
}
