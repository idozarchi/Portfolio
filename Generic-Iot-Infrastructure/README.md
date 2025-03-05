# Generic IoT Infrastructure

This project is a demonstration of an IoT management system designed for companies.
It provides a scalable and concurrent solution for registering, managing, and collecting data from IoT devices efficiently.
With this system, companies can onboard their devices and define flexible, device-specific APIs tailored to their operational needs.

The project is built with Next.js and React for the frontend and Java for the backend.

## User Requirements

1. **High Concurrency**: Efficiently handle a large volume of simultaneous requests.
2. **Modular Architecture**: Ensure components are modular for easy maintenance and scalability.
3. **Dynamic Command Addition**: Allow new commands to be added without server downtime.
4. **User Interface**: Provide a UI for seamless interaction with the server.
5. **Protocol Support**: Support multiple networking protocols for diverse IoT devices.

## [Requests](https://github.com/idozarchi/GenericIOTInfrastrucrue/tree/main/Gateway-Server/src/main/java/org/rps/command)

The project includes four fundamental requests:
- **Register Company**: Register a new company (user).
- **Register Product**: Register a new product type for a company.
- **Register Device**: Register a specific device.
- **Register Update**: Register updates sent by devices.

* The IoT infrastructure is designed to allow the addition of new commands on demand without requiring server downtime.

## Technical Capabilities

1. **[Thread Pool Management](https://github.com/idozarchi/GenericIOTInfrastrucrue/tree/main/Gateway-Server/src/main/java/org/threadpool)**: Implement a thread pool in the gateway server for efficient request handling.
2. **[Plug & Play Components](https://github.com/idozarchi/GenericIOTInfrastrucrue/tree/main/Gateway-Server/src/main/java/org/plugnplay)**: Design the gateway server with plug & play capabilities for easy integration of new components.
3. **[Protocol Handling](https://github.com/idozarchi/GenericIOTInfrastrucrue/tree/main/Gateway-Server/src/main/java/org/connectionservice)**: Develop a connection service to manage different networking protocols (HTTP, TCP, UDP).
4. **[Web Interface](https://github.com/idozarchi/GenericIOTInfrastrucrue/tree/main/IOTWebsite/IOTWebsiteFront/frontend)**: Develop a web interface to interact with the server and execute primary commands.
5. **Component Independence**: Ensure each project component operates independently.

## Main Components

1. **[Website](https://github.com/idozarchi/GenericIOTInfrastrucrue/tree/main/IOTWebsite)**
    - Developed using Next.js for the frontend and Tomcat for the backend.
    - Provides a user-friendly interface for managing IoT data.

2. **[Gateway Server](https://github.com/idozarchi/GenericIOTInfrastrucrue/tree/main/Gateway-Server/src/main/java/org)**
    - Features the `ConnectionService` supporting HTTP, TCP, and UDP protocols.
    - Includes the `RequestProcessingService` which utilizes a thread pool for efficient request handling.

## Directory Structure

- `/website`
  - Contains the Next.js and Tomcat files for the web interface.
  
- `/gateway-server`
  - `ConnectionService`
     - Manages connections using HTTP, TCP, and UDP protocols.
  - `RequestProcessingService`
     - Processes incoming requests with a thread pool.

- `/config`
  - Contains configuration files for the project, including settings for the Gateway Server and the website.

- `/scripts`
  - Includes scripts for setting up, deploying, and managing the project.

- `/docs`
  - Documentation files providing detailed information about the project, its components, and how to use and contribute to it.

## Getting Started

1. **Clone the Repository**
    ```bash
    git clone <repository-url>
    ```

2. **Navigate to the Project Directory**
    ```bash
    cd Generic-IoT-Infrastructure
    ```

3. **Install Dependencies for the Website**
    ```bash
    cd website
    npm install
    ```

4. **Run the Website**
    ```bash
    npm run dev
    ```

5. **Start the Gateway Server**
    ```bash
    cd ../gateway-server
    # Command to start the server (e.g., java -jar gateway-server.jar)
    ```

## Usage

- Access the website at `http://localhost:3000` to manage your IoT data.
- The Gateway Server will handle incoming connections and process requests using the specified protocols.

## Project Overview

The Generic IoT Infrastructure project provides a robust platform for managing data from IoT devices. It is designed to be scalable and efficient, supporting multiple networking protocols and ensuring smooth data processing. The entire project was developed from scratch with minimal reliance on external libraries.

### Internal Components

1. **Website**
    - Built with Next.js for the frontend and Tomcat for the backend, the website offers an intuitive interface for users to manage their IoT data. It allows users to view, analyze, and control their IoT devices and the data they generate.

2. **Gateway Server**
    - The Gateway Server serves as the bridge between IoT devices and the data management system. It includes:
        - **ConnectionService**: Handles connections from IoT devices using HTTP, TCP, and UDP protocols, ensuring compatibility with various devices.
        - **RequestProcessingService**: Manages the processing of incoming requests using a thread pool, allowing for concurrent handling of multiple requests and enhancing system efficiency.
