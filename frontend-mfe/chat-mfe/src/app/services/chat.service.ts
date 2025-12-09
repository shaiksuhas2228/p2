import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

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
  private apiUrl = 'http://localhost:8080/chat';

  constructor(private http: HttpClient) { }

  sendMessage(receiverUsername: string, content: string): Observable<ChatMessage> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.post<ChatMessage>(`${this.apiUrl}/send`, {
      receiverUsername,
      content
    }, { headers });
  }

  getConversation(username: string): Observable<ChatMessage[]> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.get<ChatMessage[]>(`${this.apiUrl}/conversation/${username}`, { headers });
  }

  markAsRead(username: string): Observable<string> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.post<string>(`${this.apiUrl}/mark-read/${username}`, {}, { headers });
  }

  getChatContacts(): Observable<string[]> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.get<string[]>(`${this.apiUrl}/contacts`, { headers });
  }

  getUnreadCount(username: string): Observable<number> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.get<number>(`${this.apiUrl}/unread-count/${username}`, { headers });
  }
}
