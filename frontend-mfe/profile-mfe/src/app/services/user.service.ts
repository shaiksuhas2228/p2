import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  bio?: string;
  profilePicture?: string;
  createdAt: string;
  isVerified: boolean;
}

export interface UserStats {
  userId: number;
  followersCount: number;
  followingCount: number;
  postsCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private userApiUrl = 'http://localhost:8081/api/users';
  private followApiUrl = 'http://localhost:8085/api/follow';

  constructor(private http: HttpClient) {}

  getCurrentUser(): Observable<User> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.get<User>(`${this.userApiUrl}/me`, { headers });
  }

  getUserById(userId: number): Observable<User> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.get<User>(`${this.userApiUrl}/${userId}`, { headers });
  }

  updateProfile(userData: Partial<User>): Observable<User> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.put<User>(`${this.userApiUrl}/me`, userData, { headers });
  }

  getUserStats(userId: number): Observable<UserStats> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.get<UserStats>(`${this.followApiUrl}/stats/${userId}`, { headers });
  }

  followUser(userId: number): Observable<any> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.post(`${this.followApiUrl}/follow`, { followingId: userId }, { headers });
  }

  unfollowUser(userId: number): Observable<any> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.post(`${this.followApiUrl}/unfollow`, { followingId: userId }, { headers });
  }

  isFollowing(userId: number): Observable<boolean> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.get<boolean>(`${this.followApiUrl}/is-following/${userId}`, { headers });
  }
}