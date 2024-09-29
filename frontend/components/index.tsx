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
import { Upload, Search, Download, Trash } from "lucide-react"
import { AreaChart, Area, Line, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts'
import axios from 'axios';

interface DataPoint {
  time: string
  value: number
}



const FileUpload: React.FC = () => {
  interface ReceivedFile {
    name: string;
    size: string;
    uploadedBy: string;
  }

  const [received, setReceived] = useState<ReceivedFile[]>([]);
  const [file, setFile] = useState<File | null>(null); // File type for file upload
  const [tunnelUrl, setTunnelUrl] = useState<string>(''); // String type for tunnel URL
  const [toUpload, setToUpload] = useState<File[]>([
  ])
  const [searchTerm, setSearchTerm] = useState("")
  const [dataUsage, setDataUsage] = useState<DataPoint[]>([])

  const [status, setStatus] = useState(0)
  const [usage, setUsage] = useState(0)
  const [newNode, setNewNode] = useState<string>('');

  const handleRemoveFile = (id: string) => {
    setFile(null)
  }

  const buildFile = async (uuid: string) => {
    try {
      const formData = new FormData();
      formData.append('uuid', uuid);

      const response = await fetch('http://localhost:8080/build', {
        method: 'POST',
        body: formData,
      });

      const url = await response.text();
    } catch (error) {
      console.error('Error occurred during file build:', error);
    }
  };

  const broadcast = async (uuid: string) => {
    try {
      const formData = new FormData();
      formData.append('uuid', uuid);

      const response = await fetch('http://localhost:8080/broadcast', {
        method: 'POST',
        body: formData,
      });
    } catch (error) {
      console.error('Error occurred during file build:', error);
    }
  };

  const addNode = async (node: string) => {
    try {
      const formData = new FormData();
      formData.append('tunnel', node);

      const response = await fetch('http://localhost:8080/addnode', {
        method: 'POST',
        body: formData,
      });
    } catch (error) {
      console.error('Error occurred during file build:', error);
    }
  };


  useEffect(() => {
    const interval = setInterval(() => {
      setDataUsage(prevData => {
        const newData = [...prevData, {
          time: new Date().toLocaleTimeString(),
          value: usage
        }]
        return newData.slice(-20)
      })

    }, 1000)

    return () => clearInterval(interval)
  }, [])

  useEffect(() => {
    const fetchFiles = async () => {
      try {
        const response = await axios.get('http://localhost:8080/files');

        // Assuming response.data is an array of strings in the format: "name;size;uploadedBy"
        const parsedFiles: ReceivedFile[] = response.data.map((fileString: string) => {
          const [name, size, uploadedBy] = fileString.split(';');

          // Convert size to a number and return an object of type ReceivedFile
          return {
            name: name,
            size: size,
            uploadedBy: uploadedBy,
          };
        });

        setReceived(parsedFiles);
        console.log("Received!", parsedFiles);
      } catch (error) {
        console.error('Error fetching files:', error);
      }
    };

    // Fetch files every 1000ms (1 second)
    const intervalId = setInterval(() => {
      fetchFiles();
    }, 1000);

    // Cleanup the interval on component unmount
    return () => clearInterval(intervalId);
  }, []);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setFile(e.target.files[0]);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNewNode(e.target.value);
  };

  const handleTunnelUrlChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTunnelUrl(e.target.value);
  };

  const built = (fileSize: string) => {
    const [left, right] = fileSize.split('/').map(Number);

    return left === right;
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    setUsage(100)
    e.preventDefault();
    if (!file || !tunnelUrl) {
      return;
    }

    const formData = new FormData();
    formData.append('file', file!);  // Add file to FormData  // Add tunnel URL to FormData

    const response = await fetch('http://localhost:8080/upload', {
      method: 'POST',
      body: formData,  // 'formData' holds your multipart form data
      headers: {
        // 'Content-Type' is automatically set to 'multipart/form-data' when using FormData
      },
    });

    if (!response.ok) {
      throw new Error(`Error: ${response.statusText}`);
    }

    const data = await response.json(); // or .text() depending on your response type
    console.log(data);

  };

  const handleConnection = async (type: number) => {
    setUsage(20)
    setStatus(type)
    try {
      const response = await fetch('http://localhost:8080/auth');
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }

      const data = await response.text(); // Assuming the response is just text
      console.log('Tunnel URL:', data);
      setTunnelUrl(data)
    } catch (error) {
      console.error('Error fetching tunnel URL:', error);
    }
    setUsage(0)
  }

  return (
    <div className="container mx-auto p-4 max-w-4xl min-h-screen flex flex-col justify-center">
      <div className="flex items-center mb-6">
        <h1 className="text-2xl font-black">P2P File System for&nbsp;</h1>
        <img
          src="/images/ciena.png"
          alt="Ciena Logo"
          style={{ maxHeight: '20px', width: 'auto' }}
          className="animate-fade-in mb-0.5"
        />
        {status ?
          <button onClick={() => { navigator.clipboard.writeText(tunnelUrl) }} className="group relative inline-flex ml-3 h-6 items-center justify-center overflow-hidden rounded-md bg-green-500 hover:bg-red-400 px-2 font-medium text-neutral-200 duration-500">
            <div className="relative inline-flex -translate-x-0 items-center transition group-hover:translate-x-6">
              <div className="absolute -translate-x-4 opacity-0 transition group-hover:-translate-x-6 group-hover:opacity-100">
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M9 11C9.55228 11 10 10.5523 10 10C10 9.44772 9.55228 9 9 9C8.44772 9 8 9.44772 8 10C8 10.5523 8.44772 11 9 11Z"
                    fill="currentColor"
                  />
                  <path
                    d="M9 15C8.44772 15 8 15.4477 8 16C8 16.5523 8.44772 17 9 17H15C15.5523 17 16 16.5523 16 16C16 15.4477 15.5523 15 15 15H9Z"
                    fill="currentColor"
                  />
                  <path
                    d="M16 10C16 10.5523 15.5523 11 15 11C14.4477 11 14 10.5523 14 10C14 9.44772 14.4477 9 15 9C15.5523 9 16 9.44772 16 10Z"
                    fill="currentColor"
                  />
                  <path
                    fill-rule="evenodd"
                    clip-rule="evenodd"
                    d="M22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12C2 6.47715 6.47715 2 12 2C17.5228 2 22 6.47715 22 12ZM20 12C20 16.4183 16.4183 20 12 20C7.58172 20 4 16.4183 4 12C4 7.58172 7.58172 4 12 4C16.4183 4 20 7.58172 20 12Z"
                    fill="currentColor"
                  />
                </svg>
              </div>
              <span className="pr-6">
                Connected: {tunnelUrl ? 'Click to Copy' : 'Loading...'}
              </span>
              <div className="absolute right-0 translate-x-0 opacity-100 transition group-hover:translate-x-4 group-hover:opacity-0">
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M16 13H14C14 14.1046 13.1046 15 12 15C10.8954 15 10 14.1046 10 13H8C8 15.2091 9.79086 17 12 17C14.2091 17 16 15.2091 16 13Z"
                    fill="currentColor"
                  />
                  <path
                    d="M10 10C10 10.5523 9.55228 11 9 11C8.44772 11 8 10.5523 8 10C8 9.44771 8.44772 9 9 9C9.55228 9 10 9.44771 10 10Z"
                    fill="currentColor"
                  />
                  <path
                    d="M15 11C15.5523 11 16 10.5523 16 10C16 9.44771 15.5523 9 15 9C14.4477 9 14 9.44771 14 10C14 10.5523 14.4477 11 15 11Z"
                    fill="currentColor"
                  />
                  <path
                    fill-rule="evenodd"
                    clip-rule="evenodd"
                    d="M22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12C2 6.47715 6.47715 2 12 2C17.5228 2 22 6.47715 22 12ZM20 12C20 16.4183 16.4183 20 12 20C7.58172 20 4 16.4183 4 12C4 7.58172 7.58172 4 12 4C16.4183 4 20 7.58172 20 12Z"
                    fill="currentColor"
                  />
                </svg>
              </div>
            </div>
          </button>
          :
          <button onClick={() => handleConnection(1)} className="group relative inline-flex ml-3 h-6 items-center justify-center overflow-hidden rounded-md bg-orange-400 hover:bg-green-400 px-2 font-medium text-neutral-200 duration-500">
            <div className="relative inline-flex -translate-x-0 items-center transition group-hover:translate-x-6">
              <div className="absolute -translate-x-4 opacity-0 transition group-hover:-translate-x-6 group-hover:opacity-100">
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M16 13H14C14 14.1046 13.1046 15 12 15C10.8954 15 10 14.1046 10 13H8C8 15.2091 9.79086 17 12 17C14.2091 17 16 15.2091 16 13Z"
                    fill="currentColor"
                  />
                  <path
                    d="M10 10C10 10.5523 9.55228 11 9 11C8.44772 11 8 10.5523 8 10C8 9.44771 8.44772 9 9 9C9.55228 9 10 9.44771 10 10Z"
                    fill="currentColor"
                  />
                  <path
                    d="M15 11C15.5523 11 16 10.5523 16 10C16 9.44771 15.5523 9 15 9C14.4477 9 14 9.44771 14 10C14 10.5523 14.4477 11 15 11Z"
                    fill="currentColor"
                  />
                  <path
                    fill-rule="evenodd"
                    clip-rule="evenodd"
                    d="M22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12C2 6.47715 6.47715 2 12 2C17.5228 2 22 6.47715 22 12ZM20 12C20 16.4183 16.4183 20 12 20C7.58172 20 4 16.4183 4 12C4 7.58172 7.58172 4 12 4C16.4183 4 20 7.58172 20 12Z"
                    fill="currentColor"
                  />
                </svg>
              </div>
              <span className="pr-6">Disconnected</span>
              <div className="absolute right-0 translate-x-0 opacity-100 transition group-hover:translate-x-4 group-hover:opacity-0">
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M9 11C9.55228 11 10 10.5523 10 10C10 9.44772 9.55228 9 9 9C8.44772 9 8 9.44772 8 10C8 10.5523 8.44772 11 9 11Z"
                    fill="currentColor"
                  />
                  <path
                    d="M9 15C8.44772 15 8 15.4477 8 16C8 16.5523 8.44772 17 9 17H15C15.5523 17 16 16.5523 16 16C16 15.4477 15.5523 15 15 15H9Z"
                    fill="currentColor"
                  />
                  <path
                    d="M16 10C16 10.5523 15.5523 11 15 11C14.4477 11 14 10.5523 14 10C14 9.44772 14.4477 9 15 9C15.5523 9 16 9.44772 16 10Z"
                    fill="currentColor"
                  />
                  <path
                    fill-rule="evenodd"
                    clip-rule="evenodd"
                    d="M22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12C2 6.47715 6.47715 2 12 2C17.5228 2 22 6.47715 22 12ZM20 12C20 16.4183 16.4183 20 12 20C7.58172 20 4 16.4183 4 12C4 7.58172 7.58172 4 12 4C16.4183 4 20 7.58172 20 12Z"
                    fill="currentColor"
                  />
                </svg>
              </div>
            </div>
          </button>
        }
        <div className="flex items-center">
          <input
            type="text"
            placeholder="https://"
            className="pl-2 border border-gray-300 rounded-md ml-2 w-28"
            value={newNode}
            onChange={handleInputChange}
          />
          <button onClick={() => addNode(newNode)} className="group ml-2 relative inline-flex h-6 items-center justify-center overflow-hidden rounded-md bg-neutral-950 px-6 font-medium text-neutral-200 duration-500"><div className="translate-x-0 transition group-hover:-translate-x-[150%]">Add Node</div><div className="absolute translate-x-[150%] transition group-hover:translate-x-0">Connect</div></button>
        </div>
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
            onChange={handleFileChange}
          />
        </Label>
      </div>

      {file != null ?
        <div className="h-auto overflow-y-auto mb-6 animate-fade-in">
          <form onSubmit={handleSubmit}>
            <div className="flex items-center">
              <h2 className="text-lg font-extrabold mb-2">Ready to Upload &nbsp;</h2>
              <button type="submit" className="group relative inline-flex h-6 mb-2 ml-3 items-center justify-center rounded-md bg-neutral-950 px-6 font-medium text-neutral-200"><span>Upload</span><div className="relative ml-1 h-5 w-5 overflow-hidden"><div className="absolute transition-all duration-200 group-hover:-translate-y-5 group-hover:translate-x-4"><svg width="15" height="15" viewBox="0 0 15 15" fill="none" xmlns="http://www.w3.org/2000/svg" className="h-5 w-5"><path d="M3.64645 11.3536C3.45118 11.1583 3.45118 10.8417 3.64645 10.6465L10.2929 4L6 4C5.72386 4 5.5 3.77614 5.5 3.5C5.5 3.22386 5.72386 3 6 3L11.5 3C11.6326 3 11.7598 3.05268 11.8536 3.14645C11.9473 3.24022 12 3.36739 12 3.5L12 9.00001C12 9.27615 11.7761 9.50001 11.5 9.50001C11.2239 9.50001 11 9.27615 11 9.00001V4.70711L4.35355 11.3536C4.15829 11.5488 3.84171 11.5488 3.64645 11.3536Z" fill="currentColor" fill-rule="evenodd" clip-rule="evenodd"></path></svg><svg width="15" height="15" viewBox="0 0 15 15" fill="none" xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 -translate-x-4"><path d="M3.64645 11.3536C3.45118 11.1583 3.45118 10.8417 3.64645 10.6465L10.2929 4L6 4C5.72386 4 5.5 3.77614 5.5 3.5C5.5 3.22386 5.72386 3 6 3L11.5 3C11.6326 3 11.7598 3.05268 11.8536 3.14645C11.9473 3.24022 12 3.36739 12 3.5L12 9.00001C12 9.27615 11.7761 9.50001 11.5 9.50001C11.2239 9.50001 11 9.27615 11 9.00001V4.70711L4.35355 11.3536C4.15829 11.5488 3.84171 11.5488 3.64645 11.3536Z" fill="currentColor" fill-rule="evenodd" clip-rule="evenodd"></path></svg></div></div></button>
            </div>
          </form>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>UUID</TableHead>
                <TableHead>Uploaded By</TableHead>
                <TableHead>Edit</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              <TableRow key={file.name}>
                <TableCell className="font-medium">{file.name}</TableCell>
                <TableCell>{file.size}</TableCell>
                <TableCell>You</TableCell>
                <TableCell>
                  <Dialog>
                    <DialogTrigger asChild>
                      <p className="hover:underline hover:cursor-pointer underline-offset-2 text-red-500 hover:text-red-300">Remove</p>
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
                            File Size
                          </Label>
                          <div className="col-span-3">{file.size}</div>
                        </div>
                      </div>
                      <Button className="w-full" onClick={() => handleRemoveFile(file.name)}>
                        <Trash className="mr-2 h-4 w-4" /> Confirm Removal
                      </Button>
                    </DialogContent>
                  </Dialog>
                </TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </div>
        : ''}

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
              <TableHead>UUID</TableHead>
              <TableHead>Chunk Status</TableHead>
              <TableHead>Action</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {received.map((file) => (
              <TableRow key={file.name}>
                <TableCell className="font-medium">{file.name}</TableCell>
                <TableCell>{file.size}</TableCell>
                <TableCell>{file.uploadedBy}</TableCell>
                <TableCell>
                  <a href={`/redirect/${file.size}/${file.name}`} target="_blank">
                    <button onClick={() => buildFile(file.size)} className="group mb-1 relative inline-flex h-6 items-center justify-center overflow-hidden rounded-md bg-neutral-950 px-2 font-medium text-neutral-200"><span>Build from Chunks</span><div className="ml-1 transition group-hover:translate-x-1"><svg width="15" height="15" viewBox="0 0 15 15" fill="none" xmlns="http://www.w3.org/2000/svg" className="h-5 w-5"><path d="M8.14645 3.14645C8.34171 2.95118 8.65829 2.95118 8.85355 3.14645L12.8536 7.14645C13.0488 7.34171 13.0488 7.65829 12.8536 7.85355L8.85355 11.8536C8.65829 12.0488 8.34171 12.0488 8.14645 11.8536C7.95118 11.6583 7.95118 11.3417 8.14645 11.1464L11.2929 8H2.5C2.22386 8 2 7.77614 2 7.5C2 7.22386 2.22386 7 2.5 7H11.2929L8.14645 3.85355C7.95118 3.65829 7.95118 3.34171 8.14645 3.14645Z" fill="currentColor" fill-rule="evenodd" clip-rule="evenodd"></path></svg></div></button>
                  </a>
                  <button onClick={() => broadcast(file.size)} className="group relative inline-flex h-6 items-center justify-center overflow-hidden rounded-md bg-neutral-950 px-2 font-medium text-neutral-200"><span>Broadcast for Chunks</span><div className="ml-1 transition group-hover:translate-x-1"><svg width="15" height="15" viewBox="0 0 15 15" fill="none" xmlns="http://www.w3.org/2000/svg" className="h-5 w-5"><path d="M8.14645 3.14645C8.34171 2.95118 8.65829 2.95118 8.85355 3.14645L12.8536 7.14645C13.0488 7.34171 13.0488 7.65829 12.8536 7.85355L8.85355 11.8536C8.65829 12.0488 8.34171 12.0488 8.14645 11.8536C7.95118 11.6583 7.95118 11.3417 8.14645 11.1464L11.2929 8H2.5C2.22386 8 2 7.77614 2 7.5C2 7.22386 2.22386 7 2.5 7H11.2929L8.14645 3.85355C7.95118 3.65829 7.95118 3.34171 8.14645 3.14645Z" fill="currentColor" fill-rule="evenodd" clip-rule="evenodd"></path></svg></div></button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  )
};

export default FileUpload;