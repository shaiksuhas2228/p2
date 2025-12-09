import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserProfile {
  id: number;
  username: string;
  email: string;
  bio?: string;
  profilePicture?: string;
  followersCount: number;
  followingCount: number;
  postsCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUrl = 'http://localhost:8081/api/users';

  constructor(private http: HttpClient) {}

  getProfile(userId: number): Observable<UserProfile> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.get<UserProfile>(`${this.apiUrl}/${userId}`, { headers });
  }

  updateProfile(userId: number, profile: Partial<UserProfile>): Observable<UserProfile> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.put<UserProfile>(`${this.apiUrl}/${userId}`, profile, { headers });
  }

  uploadProfilePicture(userId: number, file: File): Observable<string> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<string>(`${this.apiUrl}/${userId}/profile-picture`, formData, { headers });
  }

  followUser(userId: number): Observable<void> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.post<void>(`${this.apiUrl}/${userId}/follow`, {}, { headers });
  }

  unfollowUser(userId: number): Observable<void> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.delete<void>(`${this.apiUrl}/${userId}/follow`, { headers });
  }
}
