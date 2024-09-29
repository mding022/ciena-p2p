
# Ciena P2P File Sharing

Ciena's peer-to-peer file sharing protocol challenge for Hack the Hill 2024. Built with Spring Boot for backend file transfering using HTTPS requests, and Next.js for the frontend.




## Deployment

To deploy this project, first make sure you have docker installed.
Then, run:

```bash
  docker-compose up --build
```
Docker will run both the back and frontend. To run each separately, you can run these for each:
```bash
  cd backend
  mvn spring-boot:run
```
```bash
  cd frontend
  npm run dev
```
The backend runs on localhost:8080 and the frontend runs on localhost:3000.

## Implementation

- SSHs into a open source tunneling service to generate a random tunnel URL.
- Exposes localhost:8080 to the tunnel, people can connect by adding your tunnel as a node.
- Files are shared by the frontend calling the backend localhost:8080/upload with file metadata.
- Run a python script to chunk the file into 512-byte chunks of binary data. 
- Iterates around all of the nodes, including self, sending chunks around.
- The backend can broadcast a request to get chunks, and all nodes will send their chunks to them. Chunks should overwrite for a simple redundancy system to ensure no files will be corrupted.
- Building a file will run a python script to build the file. The script will search for the metadata.json to know what the original file information is.
- The files can be accessed by the frontend by the Spring server's public static files after building.

