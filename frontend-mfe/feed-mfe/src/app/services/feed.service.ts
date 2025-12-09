import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Post {
  id: number;
  content: string;
  userId: number;
  username: string;
  createdAt: string;
  likes: number;
  comments: number;
  media?: any[];
}

export interface FeedResponse {
  posts: Post[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
}

@Injectable({
  providedIn: 'root'
})
export class FeedService {
  private apiUrl = 'http://localhost:8083/api/feed';

  constructor(private http: HttpClient) {}

  getFeed(page: number = 0, size: number = 10): Observable<FeedResponse> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.get<FeedResponse>(`${this.apiUrl}?page=${page}&size=${size}`, { headers });
  }

  getUserFeed(userId: number, page: number = 0, size: number = 10): Observable<FeedResponse> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.get<FeedResponse>(`${this.apiUrl}/user/${userId}?page=${page}&size=${size}`, { headers });
  }
}