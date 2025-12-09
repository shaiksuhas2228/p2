# Micro-Frontend Setup Guide - Module Federation

## Overview
This guide explains how to convert the monolithic Angular frontend into micro-frontends using Module Federation.

## Architecture

### Applications Structure
```
frontend-mfe/
├── shell/              (Port 4200 - Main container)
├── feed-mfe/           (Port 4201 - Feed & Posts)
├── profile-mfe/        (Port 4202 - User Profile)
├── chat-mfe/           (Port 4203 - Chat & Messages)
└── shared-lib/         (Shared services & components)
```

## Step-by-Step Implementation

### 1. Install Module Federation

```bash
cd RevHub
npm install -g @angular/cli@18
```

### 2. Create Shell Application (Main Container)

```bash
ng new shell --routing --style=css
cd shell
ng add @angular-architects/module-federation --project shell --port 4200 --type host
```

### 3. Create Feed Micro-Frontend

```bash
cd ..
ng new feed-mfe --routing --style=css
cd feed-mfe
ng add @angular-architects/module-federation --project feed-mfe --port 4201 --type remote
```

### 4. Create Profile Micro-Frontend

```bash
cd ..
ng new profile-mfe --routing --style=css
cd profile-mfe
ng add @angular-architects/module-federation --project profile-mfe --port 4202 --type remote
```

### 5. Create Chat Micro-Frontend

```bash
cd ..
ng new chat-mfe --routing --style=css
cd chat-mfe
ng add @angular-architects/module-federation --project chat-mfe --port 4203 --type remote
```

## Configuration Files

### Shell App - webpack.config.js

```javascript
const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

module.exports = withModuleFederationPlugin({
  remotes: {
    "feedMfe": "http://localhost:4201/remoteEntry.js",
    "profileMfe": "http://localhost:4202/remoteEntry.js",
    "chatMfe": "http://localhost:4203/remoteEntry.js",
  },
  shared: {
    ...shareAll({ singleton: true, strictVersion: true, requiredVersion: 'auto' }),
  },
});
```

### Feed MFE - webpack.config.js

```javascript
const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

module.exports = withModuleFederationPlugin({
  name: 'feedMfe',
  exposes: {
    './FeedModule': './src/app/feed/feed.module.ts',
  },
  shared: {
    ...shareAll({ singleton: true, strictVersion: true, requiredVersion: 'auto' }),
  },
});
```

### Profile MFE - webpack.config.js

```javascript
const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

module.exports = withModuleFederationPlugin({
  name: 'profileMfe',
  exposes: {
    './ProfileModule': './src/app/profile/profile.module.ts',
  },
  shared: {
    ...shareAll({ singleton: true, strictVersion: true, requiredVersion: 'auto' }),
  },
});
```

### Chat MFE - webpack.config.js

```javascript
const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

module.exports = withModuleFederationPlugin({
  name: 'chatMfe',
  exposes: {
    './ChatModule': './src/app/chat/chat.module.ts',
  },
  shared: {
    ...shareAll({ singleton: true, strictVersion: true, requiredVersion: 'auto' }),
  },
});
```

## Shell App Routing

### app.routes.ts

```typescript
import { Routes } from '@angular/router';
import { loadRemoteModule } from '@angular-architects/module-federation';

export const routes: Routes = [
  {
    path: 'feed',
    loadChildren: () =>
      loadRemoteModule({
        type: 'module',
        remoteEntry: 'http://localhost:4201/remoteEntry.js',
        exposedModule: './FeedModule'
      }).then(m => m.FeedModule)
  },
  {
    path: 'profile',
    loadChildren: () =>
      loadRemoteModule({
        type: 'module',
        remoteEntry: 'http://localhost:4202/remoteEntry.js',
        exposedModule: './ProfileModule'
      }).then(m => m.ProfileModule)
  },
  {
    path: 'chat',
    loadChildren: () =>
      loadRemoteModule({
        type: 'module',
        remoteEntry: 'http://localhost:4203/remoteEntry.js',
        exposedModule: './ChatModule'
      }).then(m => m.ChatModule)
  },
  { path: '', redirectTo: '/feed', pathMatch: 'full' }
];
```

## Code Migration

### Move Existing Code

1. **Feed Module** → feed-mfe/src/app/feed/
   - feed.component.ts
   - post-card.component.ts
   - create-post.component.ts

2. **Profile Module** → profile-mfe/src/app/profile/
   - profile.component.ts
   - profile-header.component.ts
   - user-profile.component.ts

3. **Chat Module** → chat-mfe/src/app/chat/
   - dashboard.component.ts (chat section)
   - chat.service.ts

### Shared Services

Create a shared library for common services:

```bash
ng generate library shared-lib
```

Move to shared-lib:
- auth.service.ts
- post.service.ts
- notification.service.ts
- All DTOs/interfaces

## Running the Applications

### Development Mode

Open 4 terminals:

```bash
# Terminal 1 - Shell
cd shell
npm start

# Terminal 2 - Feed MFE
cd feed-mfe
npm start -- --port 4201

# Terminal 3 - Profile MFE
cd profile-mfe
npm start -- --port 4202

# Terminal 4 - Chat MFE
cd chat-mfe
npm start -- --port 4203
```

Access: http://localhost:4200

## Production Build

```bash
# Build all micro-frontends
cd feed-mfe && ng build --prod
cd ../profile-mfe && ng build --prod
cd ../chat-mfe && ng build --prod
cd ../shell && ng build --prod
```

## Deployment

Deploy each micro-frontend independently:
- Shell: main-app.yourdomain.com
- Feed: feed.yourdomain.com
- Profile: profile.yourdomain.com
- Chat: chat.yourdomain.com

Update webpack.config.js remotes URLs to production URLs.

## Benefits

1. **Independent Deployment**: Deploy each module separately
2. **Team Autonomy**: Different teams can work on different modules
3. **Technology Flexibility**: Can use different Angular versions per module
4. **Faster Builds**: Build only changed modules
5. **Better Scalability**: Scale individual modules based on load

## Challenges

1. **Shared State Management**: Need centralized state management (NgRx/Akita)
2. **Authentication**: Share auth tokens across micro-frontends
3. **Routing**: Complex routing between apps
4. **Testing**: Integration testing becomes harder
5. **Initial Setup**: More complex initial configuration

## Alternative: Keep Monolithic Frontend

For a project of this size, a monolithic frontend is actually recommended because:
- Simpler development and debugging
- Easier state management
- Single deployment
- Better performance (no remote module loading)
- Less infrastructure complexity

The backend microservices already provide good separation of concerns.

## Recommendation

**Keep the current monolithic frontend** unless you have:
- Multiple frontend teams
- Need for independent deployment cycles
- Very large application (100+ components)
- Different technology requirements per module

The current architecture (monolithic frontend + microservices backend) is a common and effective pattern.
