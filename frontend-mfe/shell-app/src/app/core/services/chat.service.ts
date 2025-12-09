import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { Client, IMessage } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

export interface ChatMessage {
  id: string;
  senderId: string;
  senderUsername: string;
  receiverId: string;
  receiverUsername: string;
  content: string;
  timestamp: string;
  read: boolean;
  messageType: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8088/api/chat';
  private stompClient: Client | null = null;
  private messageSubject = new Subject<ChatMessage>();
  public messages$ = this.messageSubject.asObservable();

  constructor(private http: HttpClient) {
    this.connect();
  }

  private connect() {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8088/ws/chat'),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });

    this.stompClient.onConnect = () => {
      console.log('WebSocket connected');
    };

    this.stompClient.onStompError = (frame) => {
      console.error('WebSocket error:', frame);
    };

    this.stompClient.activate();
  }

  subscribeToMessages(username: string) {
    if (this.stompClient) {
      const doSubscribe = () => {
        this.stompClient!.subscribe(`/topic/messages/${username}`, (message: IMessage) => {
          const chatMessage = JSON.parse(message.body);
          this.messageSubject.next(chatMessage);
        });
      };
      
      if (this.stompClient.connected) {
        doSubscribe();
      } else {
        this.stompClient.onConnect = () => {
          console.log('WebSocket connected');
          doSubscribe();
        };
      }
    }
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
  }

  sendMessage(receiverUsername: string, content: string, userId?: number, senderUsername?: string): Observable<ChatMessage> {
    const params = userId ? { currentUserId: userId.toString() } : {};
    return this.http.post<ChatMessage>(`${this.apiUrl}/send`, {
      receiverUsername,
      content,
      senderUsername
    }, { params });
  }

  getConversation(username: string, userId?: number): Observable<ChatMessage[]> {
    const params = userId ? { currentUserId: userId.toString() } : {};
    return this.http.get<ChatMessage[]>(`${this.apiUrl}/conversation/${username}`, { params });
  }

  markAsRead(username: string, userId?: number): Observable<string> {
    const params = userId ? { currentUserId: userId.toString() } : {};
    return this.http.post(`${this.apiUrl}/mark-read/${username}`, {}, { params, responseType: 'text' });
  }

  getChatContacts(userId?: number): Observable<any[]> {
    const params = userId ? { userId: userId.toString() } : {};
    return this.http.get<any[]>(`${this.apiUrl}/contacts`, { params });
  }

  getUnreadCount(username: string, userId?: number): Observable<number> {
    const params = userId ? { currentUserId: userId.toString() } : {};
    return this.http.get<number>(`${this.apiUrl}/unread-count/${username}`, { params });
  }

  deleteConversation(username: string, userId?: number): Observable<string> {
    const params = userId ? { currentUserId: userId.toString() } : {};
    return this.http.delete(`${this.apiUrl}/conversation/${username}`, { params, responseType: 'text' });
  }
}