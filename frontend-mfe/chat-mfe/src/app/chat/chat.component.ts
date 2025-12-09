import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AvatarComponent } from '../shared/components/avatar.component';
import { ChatService } from '../services/chat.service';
import { UserService, UserProfile } from '../services/user.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, AvatarComponent],
  template: `
    <div class="chat-container">
      <!-- Contacts List -->
      <div class="contacts-panel">
        <div class="panel-header">
          <h3>Messages</h3>
          <button class="new-chat-btn">✏️</button>
        </div>
        <div class="search-box">
          <input [(ngModel)]="searchQuery" placeholder="🔍 Search contacts...">
        </div>
        <div class="contacts-list">
          <div *ngFor="let contact of filteredContacts" 
               class="contact-item"
               [class.active]="selectedContact?.id === contact.id"
               (click)="selectContact(contact)">
            <app-avatar [src]="contact.avatar" [name]="contact.name" [online]="contact.online"></app-avatar>
            <div class="contact-info">
              <h4>{{contact.name}}</h4>
              <p class="last-message">{{contact.lastMessage}}</p>
            </div>
            <div class="contact-meta">
              <span class="time">{{contact.time}}</span>
              <span *ngIf="contact.unread" class="unread-badge">{{contact.unread}}</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Chat Area -->
      <div class="chat-panel">
        <div *ngIf="!selectedContact" class="empty-chat">
          <h2>💬 Select a conversation</h2>
          <p>Choose a contact to start messaging</p>
        </div>
        
        <div *ngIf="selectedContact" class="chat-content">
          <!-- Chat Header -->
          <div class="chat-header">
            <div class="chat-user">
              <app-avatar [src]="selectedContact.avatar" [name]="selectedContact.name" [online]="selectedContact.online"></app-avatar>
              <div>
                <h3>{{selectedContact.name}}</h3>
                <span class="status">{{selectedContact.online ? 'Online' : 'Offline'}}</span>
              </div>
            </div>
            <button class="more-btn">⋮</button>
          </div>
          
          <!-- Messages -->
          <div class="messages-area">
            <div *ngFor="let message of messages" 
                 class="message"
                 [class.sent]="message.sent"
                 [class.received]="!message.sent">
              <div class="message-bubble">
                <p>{{message.content}}</p>
                <span class="message-time">{{message.time}}</span>
              </div>
            </div>
          </div>
          
          <!-- Message Input -->
          <div class="message-input">
            <button class="attach-btn">📎</button>
            <input [(ngModel)]="newMessage" 
                   (keyup.enter)="sendMessage()"
                   placeholder="Type a message...">
            <button class="send-btn" (click)="sendMessage()">
              ✈️
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .chat-container {
      display: flex;
      height: calc(100vh - 100px);
      background: linear-gradient(135deg, rgba(240, 253, 250, 0.9), rgba(255, 255, 255, 0.95));
      border: 2px solid rgba(20, 184, 166, 0.2);
      border-radius: 16px;
      overflow: hidden;
      box-shadow: 0 4px 20px rgba(20, 184, 166, 0.1);
    }
    .contacts-panel {
      width: 350px;
      border-right: 2px solid rgba(20, 184, 166, 0.2);
      display: flex;
      flex-direction: column;
      background: white;
    }
    .panel-header {
      padding: 20px;
      border-bottom: 2px solid rgba(20, 184, 166, 0.2);
      display: flex;
      justify-content: space-between;
      align-items: center;
      background: linear-gradient(135deg, #14B8A6, #F97316);
      color: white;
    }
    .panel-header h3 {
      margin: 0;
      font-size: 20px;
    }
    .new-chat-btn {
      background: rgba(255, 255, 255, 0.2);
      border: none;
      border-radius: 50%;
      width: 40px;
      height: 40px;
      font-size: 20px;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    .new-chat-btn:hover {
      background: rgba(255, 255, 255, 0.3);
      transform: scale(1.1);
    }
    .search-box {
      padding: 15px;
      border-bottom: 2px solid rgba(20, 184, 166, 0.2);
    }
    .search-box input {
      width: 100%;
      padding: 10px 15px;
      border: 2px solid rgba(20, 184, 166, 0.2);
      border-radius: 25px;
      font-size: 14px;
    }
    .contacts-list {
      flex: 1;
      overflow-y: auto;
    }
    .contact-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 15px;
      cursor: pointer;
      transition: all 0.3s ease;
      border-bottom: 1px solid rgba(20, 184, 166, 0.1);
    }
    .contact-item:hover {
      background: rgba(20, 184, 166, 0.05);
    }
    .contact-item.active {
      background: rgba(20, 184, 166, 0.1);
      border-left: 4px solid #14B8A6;
    }
    .contact-info {
      flex: 1;
      min-width: 0;
    }
    .contact-info h4 {
      margin: 0;
      font-size: 16px;
      color: #134E4A;
    }
    .last-message {
      margin: 5px 0 0 0;
      font-size: 14px;
      color: #666;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .contact-meta {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 5px;
    }
    .time {
      font-size: 12px;
      color: #999;
    }
    .unread-badge {
      background: linear-gradient(135deg, #14B8A6, #F97316);
      color: white;
      border-radius: 12px;
      padding: 2px 8px;
      font-size: 12px;
      font-weight: 600;
    }
    .chat-panel {
      flex: 1;
      display: flex;
      flex-direction: column;
    }
    .empty-chat {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      color: #666;
    }
    .empty-chat h2 {
      margin: 0 0 10px 0;
      color: #134E4A;
    }
    .chat-content {
      flex: 1;
      display: flex;
      flex-direction: column;
    }
    .chat-header {
      padding: 20px;
      border-bottom: 2px solid rgba(20, 184, 166, 0.2);
      display: flex;
      justify-content: space-between;
      align-items: center;
      background: white;
    }
    .chat-user {
      display: flex;
      align-items: center;
      gap: 12px;
    }
    .chat-user h3 {
      margin: 0;
      color: #134E4A;
    }
    .status {
      font-size: 14px;
      color: #10B981;
    }
    .more-btn {
      background: none;
      border: none;
      font-size: 24px;
      cursor: pointer;
      color: #666;
    }
    .messages-area {
      flex: 1;
      padding: 20px;
      overflow-y: auto;
      display: flex;
      flex-direction: column;
      gap: 15px;
    }
    .message {
      display: flex;
    }
    .message.sent {
      justify-content: flex-end;
    }
    .message.received {
      justify-content: flex-start;
    }
    .message-bubble {
      max-width: 60%;
      padding: 12px 16px;
      border-radius: 16px;
      position: relative;
    }
    .message.sent .message-bubble {
      background: linear-gradient(135deg, #14B8A6, #F97316);
      color: white;
      border-bottom-right-radius: 4px;
    }
    .message.received .message-bubble {
      background: white;
      border: 2px solid rgba(20, 184, 166, 0.2);
      color: #1F2937;
      border-bottom-left-radius: 4px;
    }
    .message-bubble p {
      margin: 0 0 5px 0;
    }
    .message-time {
      font-size: 11px;
      opacity: 0.7;
    }
    .message-input {
      padding: 20px;
      border-top: 2px solid rgba(20, 184, 166, 0.2);
      display: flex;
      gap: 10px;
      background: white;
    }
    .attach-btn {
      background: rgba(20, 184, 166, 0.1);
      border: none;
      border-radius: 50%;
      width: 40px;
      height: 40px;
      font-size: 20px;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    .attach-btn:hover {
      background: rgba(20, 184, 166, 0.2);
    }
    .message-input input {
      flex: 1;
      padding: 12px 16px;
      border: 2px solid rgba(20, 184, 166, 0.2);
      border-radius: 25px;
      font-size: 14px;
    }
    .send-btn {
      background: linear-gradient(135deg, #14B8A6, #F97316);
      color: white;
      border: none;
      border-radius: 50%;
      width: 40px;
      height: 40px;
      font-size: 20px;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    .send-btn:hover {
      transform: scale(1.1);
      box-shadow: 0 4px 15px rgba(20, 184, 166, 0.3);
    }
  `]
})
export class ChatComponent implements OnInit {
  contacts: any[] = [];
  selectedContact: any = null;
  messages: any[] = [];
  newMessage = '';
  searchQuery = '';

  constructor(
    private chatService: ChatService,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.loadChatContacts();
  }

  loadChatContacts() {
    this.chatService.getChatContacts().subscribe({
      next: (usernames: string[]) => {
        if (usernames && usernames.length > 0) {
          const userRequests = usernames.map(username => 
            this.userService.getUserProfile(username)
          );
          
          forkJoin(userRequests).subscribe({
            next: (profiles: UserProfile[]) => {
              this.contacts = profiles.map(profile => ({
                id: profile.id,
                name: profile.fullName || profile.username,
                username: profile.username,
                avatar: profile.profilePictureUrl || '',
                lastMessage: 'Start chatting...',
                time: 'now',
                unread: 0,
                online: false
              }));
            },
            error: (err) => {
              console.error('Error loading user profiles:', err);
              this.loadDemoContacts();
            }
          });
        } else {
          this.loadDemoContacts();
        }
      },
      error: (err) => {
        console.error('Error loading chat contacts:', err);
        this.loadDemoContacts();
      }
    });
  }

  loadDemoContacts() {
    this.contacts = [
      { id: 1, name: 'Alice Johnson', username: 'alice', avatar: '', lastMessage: 'Hey! How are you?', time: '2m', unread: 2, online: true },
      { id: 2, name: 'Bob Smith', username: 'bob', avatar: '', lastMessage: 'See you tomorrow!', time: '1h', unread: 0, online: false },
      { id: 3, name: 'Carol White', username: 'carol', avatar: '', lastMessage: 'Thanks for your help!', time: '3h', unread: 1, online: true },
      { id: 4, name: 'David Brown', username: 'david', avatar: '', lastMessage: 'Let me know when you\'re free', time: '1d', unread: 0, online: false }
    ];
  }

  get filteredContacts() {
    if (!this.searchQuery) return this.contacts;
    return this.contacts.filter(c => 
      c.name.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  selectContact(contact: any) {
    this.selectedContact = contact;
    this.loadMessages();
  }

  loadMessages() {
    if (this.selectedContact && this.selectedContact.username) {
      this.chatService.getConversation(this.selectedContact.username).subscribe({
        next: (chatMessages) => {
          this.messages = chatMessages.map(msg => ({
            content: msg.content,
            time: new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
            sent: msg.senderUsername !== this.selectedContact.username
          }));
          
          if (this.messages.length === 0) {
            this.loadDemoMessages();
          }
        },
        error: (err) => {
          console.error('Error loading messages:', err);
          this.loadDemoMessages();
        }
      });
    } else {
      this.loadDemoMessages();
    }
  }

  loadDemoMessages() {
    this.messages = [
      { content: 'Hi there!', time: '10:30 AM', sent: false },
      { content: 'Hello! How can I help you?', time: '10:31 AM', sent: true },
      { content: 'I wanted to ask about the project', time: '10:32 AM', sent: false },
      { content: 'Sure, what would you like to know?', time: '10:33 AM', sent: true }
    ];
  }

  sendMessage() {
    if (this.newMessage.trim() && this.selectedContact) {
      const messageContent = this.newMessage;
      
      if (this.selectedContact.username) {
        this.chatService.sendMessage(this.selectedContact.username, messageContent).subscribe({
          next: (sentMessage) => {
            this.messages.push({
              content: messageContent,
              time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
              sent: true
            });
            this.newMessage = '';
          },
          error: (err) => {
            console.error('Error sending message:', err);
            this.messages.push({
              content: messageContent,
              time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
              sent: true
            });
            this.newMessage = '';
          }
        });
      } else {
        this.messages.push({
          content: messageContent,
          time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          sent: true
        });
        this.newMessage = '';
      }
    }
  }
}
