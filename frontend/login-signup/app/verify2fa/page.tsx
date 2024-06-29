"use client"

import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import {useRouter} from "next/navigation";
import React, { useState } from "react"
import axios, {AxiosError} from 'axios';

interface ErrorResponse {
  message?: string;
}

export default function Verify2FA() {
  const router = useRouter();


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
      <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
        <div className="text-center">
          <h2 className="text-2xl font-bold">Two Factor Authentication</h2>
          <p className="text-muted-foreground">Enter 6 digits code generated by the Authenticator App</p>
        </div>
        <div className="space-y-4">
          <div className="space-y-2">
            <Input placeholder="Enter code" value={code} onChange={e => setCode(e.target.value)}/>
          </div>
          <Button className="bg-black text-white w-full" onClick={handleVerifyButton}>Verify</Button>
        </div>
      </div>
    </div>
  )
}