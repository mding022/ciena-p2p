// Basic font styling page
import { Poppins } from 'next/font/google'
import { Space_Mono } from 'next/font/google'
import { cn } from '@/lib/utils'
import './globals.css'
import { ReactNode } from 'react'

const fontHeading = Poppins({
    subsets: ['latin'],
    display: 'swap',
    variable: '--font-heading',
    weight: ['100', '200', '300', '400', '500', '600', '700', '800', '900'],
})

const fontBody = Space_Mono({
    weight: ["400", "700"],
    subsets: ['latin'],
    display: 'swap',
    variable: '--font-body',
})

interface LayoutProps {
    children: ReactNode 
}

export default function Layout({ children }: LayoutProps) {
    return (
        <html lang="en">
            <body
                className={cn(
                    'antialiased',
                    fontHeading.variable,
                    fontBody.variable
                )}
            >
                {children}
            </body>
        </html>
    )
}