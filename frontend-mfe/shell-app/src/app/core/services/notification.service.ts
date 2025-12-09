import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Notification {
  id: string;
  type: string;
  message: string;
  readStatus: boolean;
  createdDate: string;
  fromUserId?: string;
  fromUsername?: string;
  fromUserProfilePicture?: string;
  followRequestId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:8085/api/notifications';

  constructor(private http: HttpClient) { }

  getNotifications(userId?: number): Observable<Notification[]> {
    const url = userId ? `${this.apiUrl}/${userId}` : this.apiUrl;
    return this.http.get<Notification[]>(url);
  }

  markAsRead(id: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/read`, {}, { responseType: 'text' });
  }

  getUnreadCount(userId?: number): Observable<number> {
    const url = userId ? `${this.apiUrl}/${userId}/count` : `${this.apiUrl}/unread-count`;
    return this.http.get<number>(url);
  }

  acceptFollowRequest(followId: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/follow-request/${followId}/accept`, {}, { responseType: 'text' });
  }

  rejectFollowRequest(followId: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/follow-request/${followId}/reject`, {}, { responseType: 'text' });
  }

  deleteNotification(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }
}