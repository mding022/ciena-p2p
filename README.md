
## Implementation

- SSHs into a open source tunneling service to generate a random tunnel URL.
- Exposes localhost:8080 to the tunnel, people can connect by adding your tunnel as a node.
- Files are shared by the frontend calling the backend localhost:8080/upload with file metadata.
- Run a python script to chunk the file into 512-byte chunks of binary data. 
- Iterates around all of the nodes, including self, sending chunks around.
- The backend can broadcast a request to get chunks, and all nodes will send their chunks to them. Chunks should overwrite for a simple redundancy system to ensure no files will be corrupted.
- Building a file will run a python script to build the file. The script will search for the metadata.json to know what the original file information is.
- The files can be accessed by the frontend by the Spring server's public static files after building.

