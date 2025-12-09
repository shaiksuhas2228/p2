import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserProfile {
  id: number;
  username: string;
  email: string;
  fullName: string;
  bio?: string;
  profilePictureUrl?: string;
  createdAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8081/users';

  constructor(private http: HttpClient) { }

  getUserProfile(username: string): Observable<UserProfile> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.get<UserProfile>(`${this.apiUrl}/profile/${username}`, { headers });
  }

  getCurrentUser(): Observable<UserProfile> {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    return this.http.get<UserProfile>(`${this.apiUrl}/me`, { headers });
  }
}
