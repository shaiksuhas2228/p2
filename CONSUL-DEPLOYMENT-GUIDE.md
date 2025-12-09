# RevHub with Consul Service Discovery - Deployment Guide

## What is Consul?
Consul is a service discovery tool that allows microservices to find and communicate with each other dynamically.

## Architecture
```
Consul (Port 8500) - Service Registry
├── User Service (8081) - Registers itself
├── Post Service (8082) - Registers itself  
├── Chat Service (8083) - Registers itself
└── Notification Service (8084) - Registers itself
```

---

## Step 1: Push Updated Code to GitHub

```bash
cd c:\Users\acer\Downloads\RevHubTeam7final_2\RevHubTeam7final\RevHub
git add .
git commit -m "Added Spring Cloud Consul for service discovery"
git push origin main
```

---

## Step 2: Launch EC2 & Connect

1. AWS Console → EC2 → Launch Instance
2. Settings:
   - AMI: Amazon Linux 2023
   - Type: t2.medium
   - Security Group: Add ports 22, 80, 8081-8084, **8500** (Consul)
3. Connect: `ssh -i revhub-key.pem ec2-user@YOUR_IP`

---

## Step 3: Install Prerequisites

```bash
# Update system
sudo yum update -y

# Install Java 17
sudo yum install java-17-amazon-corretto-devel -y

# Install Maven
sudo wget https://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven

# Install MongoDB
sudo tee /etc/yum.repos.d/mongodb-org-7.0.repo > /dev/null <<EOF
[mongodb-org-7.0]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/amazon/2/mongodb-org/7.0/x86_64/
gpgcheck=1
enabled=1
gpgkey=https://www.mongodb.org/static/pgp/server-7.0.asc
EOF

sudo yum install -y mongodb-org
sudo systemctl start mongod
sudo systemctl enable mongod
```

---

## Step 4: Install Consul

```bash
# Download Consul
cd /tmp
wget https://releases.hashicorp.com/consul/1.17.0/consul_1.17.0_linux_amd64.zip

# Install unzip
sudo yum install -y unzip

# Extract Consul
unzip consul_1.17.0_linux_amd64.zip

# Move to bin
sudo mv consul /usr/local/bin/

# Verify
consul --version

# Create Consul directory
sudo mkdir -p /etc/consul.d
sudo mkdir -p /opt/consul

# Create Consul config
sudo tee /etc/consul.d/consul.json > /dev/null <<EOF
{
  "datacenter": "dc1",
  "data_dir": "/opt/consul",
  "log_level": "INFO",
  "server": true,
  "bootstrap_expect": 1,
  "ui": true,
  "client_addr": "0.0.0.0",
  "bind_addr": "0.0.0.0"
}
EOF

# Create Consul service
sudo tee /etc/systemd/system/consul.service > /dev/null <<EOF
[Unit]
Description=Consul Service Discovery Agent
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=root
ExecStart=/usr/local/bin/consul agent -config-dir=/etc/consul.d
ExecReload=/bin/kill -HUP \$MAINPID
KillSignal=SIGTERM
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

# Start Consul
sudo systemctl daemon-reload
sudo systemctl start consul
sudo systemctl enable consul

# Verify Consul is running
consul members
```

---

## Step 5: Clone & Build Services

```bash
cd /home/ec2-user
git clone https://github.com/shaiksuhas2228/p2.git
cd p2/microservices

# Build User Service
cd user-service
mvn clean package -DskipTests
nohup java -jar target/*.jar > user-service.log 2>&1 &

# Build Post Service  
cd ../post-service
mvn clean package -DskipTests
nohup java -jar target/*.jar > post-service.log 2>&1 &

# Build Chat Service
cd ../chat-service
mvn clean package -DskipTests
nohup java -jar target/*.jar > chat-service.log 2>&1 &

# Build Notification Service
cd ../notification-service
mvn clean package -DskipTests
nohup java -jar target/*.jar > notification-service.log 2>&1 &
```

---

## Step 6: Verify Services in Consul

```bash
# Check Consul UI
# Open browser: http://YOUR_EC2_IP:8500

# Or check via CLI
consul catalog services

# You should see:
# - user-service
# - post-service
# - chat-service
# - notification-service
```

---

## Step 7: Install Node.js & Deploy Frontend

```bash
# Install Node.js
curl -sL https://rpm.nodesource.com/setup_18.x | sudo bash -
sudo yum install -y nodejs

# Install Nginx
sudo amazon-linux-extras install nginx1 -y

# Update frontend environment
cd /home/ec2-user/p2/frontend-mfe/shell-app/src/environments
nano environment.prod.ts
```

Update with your EC2 IP:
```typescript
export const environment = {
  production: true,
  apiUrl: 'http://YOUR_EC2_IP:8081',
  postServiceUrl: 'http://YOUR_EC2_IP:8082',
  chatServiceUrl: 'http://YOUR_EC2_IP:8083',
  notificationServiceUrl: 'http://YOUR_EC2_IP:8084',
  wsUrl: 'ws://YOUR_EC2_IP:8083/ws'
};
```

```bash
# Build frontend
cd /home/ec2-user/p2/frontend-mfe/shell-app
npm install
npm run build

# Deploy to Nginx
sudo rm -rf /usr/share/nginx/html/*
sudo cp -r dist/shell-app/browser/* /usr/share/nginx/html/

# Configure Nginx
sudo tee /etc/nginx/conf.d/revhub.conf > /dev/null <<EOF
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files \$uri \$uri/ /index.html;
    }
}
EOF

# Start Nginx
sudo systemctl start nginx
sudo systemctl enable nginx
```

---

## Step 8: Test Everything

### Test Consul UI
```
http://YOUR_EC2_IP:8500
```

### Test Application
```
http://YOUR_EC2_IP
```

### Check Service Health
```bash
# Check all services registered
consul catalog services

# Check service health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
```

---

## Benefits of Consul

✅ **Service Discovery** - Services find each other automatically
✅ **Health Checks** - Consul monitors service health
✅ **Load Balancing** - Can distribute requests
✅ **Service Registry** - Central registry of all services
✅ **Professional** - Industry-standard tool

---

## Useful Commands

### Check Consul Status
```bash
consul members
consul catalog services
consul catalog nodes
```

### Restart Services
```bash
pkill -f java
cd /home/ec2-user/p2/microservices
# Then start each service again
```

### View Logs
```bash
tail -f /home/ec2-user/p2/microservices/user-service/user-service.log
```

### Restart Consul
```bash
sudo systemctl restart consul
sudo systemctl status consul
```

---

## Architecture Diagram

```
┌─────────────────────────────────────────┐
│           Browser (Port 80)             │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│         Nginx (Frontend)                │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│      Consul (Port 8500)                 │
│      Service Registry                   │
└──────────────┬──────────────────────────┘
               │
       ┌───────┴───────┬───────┬───────┐
       ▼               ▼       ▼       ▼
   User(8081)    Post(8082) Chat(8083) Notif(8084)
       │               │       │       │
       └───────┬───────┴───────┴───────┘
               ▼
         MongoDB (27017)
```

---

## Cost

- **EC2 t2.micro**: Free tier (750 hours/month)
- **Consul**: Free (open source)
- **Total**: $0 for first 12 months

---

**Your microservices now use Consul for service discovery!** 🎉

Access:
- **Application**: http://YOUR_EC2_IP
- **Consul UI**: http://YOUR_EC2_IP:8500
