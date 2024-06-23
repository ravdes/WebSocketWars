"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"

export default function Account() {
  const [isSignUp, setIsSignUp] = useState(false)
  return (
    <div className="flex min-h-screen items-center justify-center bg-[#8c7ae6]">
      <div className="w-full max-w-md space-y-8">
        {isSignUp ? (
          <div className="rounded-lg border bg-[#f1f1f1] p-8 shadow-lg">
            <div className="text-center">
              <div className="flex items-center justify-center">
                <UserIcon className="mr-2 h-6 w-6" />
                <h2 className="text-2xl font-bold">Hello User</h2>
              </div>
              <p className="text-muted-foreground">What are we doing today?</p>
            </div>
            <form className="mt-6 space-y-4">
              <Button
                type="button"
                className="w-full rounded-lg bg-[#6c5ce7] py-3 px-6 font-bold text-white hover:bg-[#5b44e0] focus:outline-none focus:ring-2 focus:ring-[#6c5ce7] focus:ring-offset-2"
              >
                Enable 2FA
              </Button>
              <Button
                type="submit"
                className="w-full rounded-lg bg-[#2ecc71] py-3 px-6 font-bold text-white hover:bg-[#27ae60] focus:outline-none focus:ring-2 focus:ring-[#2ecc71] focus:ring-offset-2"
              >
                Create Game
              </Button>
              <Button
                type="button"
                className="w-full rounded-lg bg-[#e74c3c] py-3 px-6 font-bold text-white hover:bg-[#c0392b] focus:outline-none focus:ring-2 focus:ring-[#e74c3c] focus:ring-offset-2"
              >
                Logout
              </Button>
            </form>
          </div>
        ) : (
          <div className="rounded-lg border bg-[#f1f1f1] p-8 shadow-lg">
            <div className="text-center">
              <div className="flex items-center justify-center">
                <UserIcon className="mr-2 h-6 w-6" />
                <h2 className="text-2xl font-bold">Hello User</h2>
              </div>
              <p className="text-muted-foreground">What are we doing today?</p>
            </div>
            <form className="mt-6 space-y-4">
              <Button
                type="button"
                className="w-full rounded-lg bg-[#6c5ce7] py-3 px-6 font-bold text-white hover:bg-[#5b44e0] focus:outline-none focus:ring-2 focus:ring-[#6c5ce7] focus:ring-offset-2"
              >
                Enable 2FA
              </Button>
              <Button
                type="submit"
                className="w-full rounded-lg bg-[#2ecc71] py-3 px-6 font-bold text-white hover:bg-[#27ae60] focus:outline-none focus:ring-2 focus:ring-[#2ecc71] focus:ring-offset-2"
              >
                Create Game
              </Button>
              <Button
                type="button"
                className="w-full rounded-lg bg-[#e74c3c] py-3 px-6 font-bold text-white hover:bg-[#c0392b] focus:outline-none focus:ring-2 focus:ring-[#e74c3c] focus:ring-offset-2"
              >
                Logout
              </Button>
            </form>
          </div>
        )}
      </div>
    </div>
  )
}

function UserIcon(props) {
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
      <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
      <circle cx="12" cy="7" r="4" />
    </svg>
  )
}
