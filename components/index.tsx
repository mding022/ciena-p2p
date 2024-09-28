"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Upload, Search, Download } from "lucide-react"
import { AreaChart, Area, Line, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts'

interface File {
  id: string
  name: string
  size: string
  uploadedBy: string
}

interface DataPoint {
  time: string
  value: number
}


export default function Index() {
  const [files, setFiles] = useState<File[]>([
    { id: "1", name: "abc.txt", size: "2.5 MB", uploadedBy: "suxo@node1" },
    { id: "2", name: "dullmoment.gif", size: "1.8 MB", uploadedBy: "baindur@node2" },
    { id: "3", name: "info.png", size: "3.2 MB", uploadedBy: "antoine@lavigne.net" },
  ])
  const [searchTerm, setSearchTerm] = useState("")
  const [dataUsage, setDataUsage] = useState<DataPoint[]>([])

  useEffect(() => {
    const interval = setInterval(() => {
      setDataUsage(prevData => {
        const newData = [...prevData, {
          time: new Date().toLocaleTimeString(),
          value: Math.floor(Math.random() * 100)
        }]
        return newData.slice(-20)
      })
    }, 1000)

    return () => clearInterval(interval)
  }, [])

  const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (file) {
      const newFile: File = {
        id: Date.now().toString(),
        name: file.name,
        size: `${(file.size / 1024 / 1024).toFixed(2)} MB`,
        uploadedBy: "You",
      }
      setFiles([...files, newFile])
    }
  }

  const filteredFiles = files.filter((file) =>
    file.name.toLowerCase().includes(searchTerm.toLowerCase())
  )

  return (
    <div className="container mx-auto p-4 max-w-4xl min-h-screen flex flex-col justify-center">
      <div className="flex items-center mb-6">
        <h1 className="text-2xl font-black">P2P File System for&nbsp;</h1>
        <img
          src="/images/ciena.png"
          alt="Ciena Logo"
          style={{ maxHeight: '20px', width: 'auto' }}
          className="animate-fade-in"
        />
      </div>
      <div className="mb-6 animate-fade-in">
        <Label htmlFor="file-upload" className="cursor-pointer">
          <div className="flex items-center justify-center w-full h-32 border-2 border-dashed border-gray-300 rounded-lg hover:border-gray-400 transition-colors">
            <div className="text-center">
              <Upload className="mx-auto h-8 w-8 text-gray-400" />
              <span className="mt-2 block text-sm font-medium text-gray-700">
                Upload a file
              </span>
            </div>
          </div>
          <Input
            id="file-upload"
            type="file"
            className="hidden"
            onChange={handleFileUpload}
          />
        </Label>
      </div>

      <div className="mb-6 animate-fade-in">
        <h2 className="text-lg font-extrabold mb-2">Data Usage</h2>
        <div className="h-32 bg-white rounded-lg shadow-md p-4">
          <ResponsiveContainer width="100%" height="100%">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={dataUsage}>
                <XAxis dataKey="time" />
                <YAxis
                  domain={[0, 100]}
                  ticks={[0, 100]}
                  tickFormatter={(value) => `${value}%`}
                />
                <Tooltip formatter={(value) => [`${value}%`, 'Value']} />
                <Area
                  type="monotone"
                  dataKey="value"
                  stroke="#8884d8"
                  fillOpacity={0.4}
                  fill="#8884d8"
                />
                <Line
                  type="monotone"
                  dataKey="value"
                  stroke="#8884d8"
                  strokeWidth={2}
                  dot={false}
                />
              </AreaChart>
            </ResponsiveContainer>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="mb-6 animate-fade-in">
        <div className="relative">
          <Search className="absolute left-2 top-2.5 h-4 w-4 text-gray-400" />
          <Input
            type="text"
            placeholder="Search files..."
            className="pl-8"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      <div className="h-64 overflow-y-auto mb-6 animate-fade-in">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Size</TableHead>
              <TableHead>Uploaded By</TableHead>
              <TableHead>Action</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredFiles.map((file) => (
              <TableRow key={file.id}>
                <TableCell className="font-medium">{file.name}</TableCell>
                <TableCell>{file.size}</TableCell>
                <TableCell>{file.uploadedBy}</TableCell>
                <TableCell>
                  <Dialog>
                    <DialogTrigger asChild>
                      <Button variant="ghost" size="sm">
                        Details
                      </Button>
                    </DialogTrigger>
                    <DialogContent>
                      <DialogHeader>
                        <DialogTitle>{file.name}</DialogTitle>
                      </DialogHeader>
                      <div className="grid gap-4 py-4">
                        <div className="grid grid-cols-4 items-center gap-4">
                          <Label htmlFor="name" className="text-right">
                            Name
                          </Label>
                          <div className="col-span-3">{file.name}</div>
                        </div>
                        <div className="grid grid-cols-4 items-center gap-4">
                          <Label htmlFor="size" className="text-right">
                            Size
                          </Label>
                          <div className="col-span-3">{file.size}</div>
                        </div>
                        <div className="grid grid-cols-4 items-center gap-4">
                          <Label htmlFor="uploadedBy" className="text-right">
                            Uploaded By
                          </Label>
                          <div className="col-span-3">{file.uploadedBy}</div>
                        </div>
                      </div>
                      <Button className="w-full">
                        <Download className="mr-2 h-4 w-4" /> Download
                      </Button>
                    </DialogContent>
                  </Dialog>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  )
}
