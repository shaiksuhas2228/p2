# How to Use Consul - Simple Guide

## What Consul Does

Consul automatically registers all your microservices so they can find each other. Instead of hardcoding URLs, services discover each other through Consul.

---

## Local Development (Windows)

### Step 1: Install Consul

```bash
# Download Consul for Windows
# Visit: https://www.consul.io/downloads
# Download: consul_1.17.0_windows_amd64.zip

# Extract to C:\consul\
# Add C:\consul to PATH
```

### Step 2: Start Consul

```bash
# Open Command Prompt
consul agent -dev
```

This starts Consul on **http://localhost:8500**

### Step 3: Start Your Microservices

```bash
# Start all services (they auto-register with Consul)
cd microservices
start-all-services.bat
```

### Step 4: View Consul UI

Open browser: **http://localhost:8500/ui**

You'll see all 7 services registered:
- user-service (8081)
- post-service (8082)
- feed-service (8083)
- chat-service (8086)
- follow-service (8085)
- notification-service (8086)
- search-service (8087)

---

## What You See in Consul UI

### Services Tab
- Lists all registered microservices
- Shows health status (green = healthy, red = unhealthy)
- Shows number of instances per service

### Nodes Tab
- Shows all servers running Consul
- In dev mode, you'll see 1 node (your local machine)

### Key/Value Tab
- Store configuration (not used in your project yet)

---

## How Services Auto-Register

Your services already have this code (we added it):

**pom.xml** - Consul dependency
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
```

**application.properties** - Consul config
```properties
spring.cloud.consul.host=localhost
spring.cloud.consul.port=8500
spring.cloud.consul.discovery.enabled=true
```

**Main Application Class** - Enable discovery
```java
@EnableDiscoveryClient
@SpringBootApplication
public class UserServiceApplication {
    // ...
}
```

That's it! Service auto-registers when it starts.

---

## Testing Consul

### Check if Consul is Running
```bash
consul members
```

### List All Services
```bash
consul catalog services
```

### Check Service Health
```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

---

## Common Commands

### Start Consul (Dev Mode)
```bash
consul agent -dev
```

### Stop Consul
```bash
# Press Ctrl+C in the Consul terminal
```

### View Consul Logs
```bash
# Logs appear in the terminal where you started Consul
```

---

## Production Deployment (AWS EC2)

Follow **CONSUL-DEPLOYMENT-GUIDE.md** for complete AWS deployment.

Quick summary:
1. Install Consul on EC2
2. Start Consul as a service
3. Deploy all microservices
4. Services auto-register with Consul
5. Access Consul UI at http://YOUR_EC2_IP:8500

---

## Benefits for Your Project

✅ **Auto-Discovery**: Services find each other automatically
✅ **Health Monitoring**: Consul checks if services are alive
✅ **Professional**: Industry-standard tool (used by Netflix, Uber)
✅ **Required**: Your instructor wants Spring Cloud + Consul

---

## Troubleshooting

### Service Not Showing in Consul?
1. Check if Consul is running: `consul members`
2. Check service logs for errors
3. Verify application.properties has Consul config
4. Restart the service

### Consul UI Not Loading?
1. Check if Consul is running on port 8500
2. Try: http://localhost:8500/ui
3. Check firewall settings

### Service Shows Unhealthy?
1. Check if service is actually running
2. Check /actuator/health endpoint
3. Review service logs

---

## Quick Start (Right Now)

```bash
# Terminal 1: Start Consul
consul agent -dev

# Terminal 2: Start Backend Services
cd microservices
start-all-services.bat

# Terminal 3: Start Frontend
cd frontend-mfe\shell-app
npm start

# Browser 1: View Consul UI
http://localhost:8500/ui

# Browser 2: Use Application
http://localhost:4200
```

Done! All services are now using Consul for service discovery.
