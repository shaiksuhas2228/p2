import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface JwtResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8081/api/auth';
  private currentUserSubject = new BehaviorSubject<any>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const userStr = localStorage.getItem('user');
        const user = userStr ? JSON.parse(userStr) : null;
        if (user) {
          this.currentUserSubject.next(user);
        }
      } catch (e) {
        console.error('Error parsing user data:', e);
        localStorage.removeItem('user');
        localStorage.removeItem('token');
      }
    }
  }

  login(credentials: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          localStorage.setItem('token', response.token);
          localStorage.setItem('user', JSON.stringify({
            id: response.id,
            username: response.username,
            email: response.email
          }));
          this.currentUserSubject.next({
            id: response.id,
            username: response.username,
            email: response.email
          });
        })
      );
  }

  register(userData: RegisterRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/register`, userData, { responseType: 'text' });
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {}).pipe(
      tap(() => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        this.currentUserSubject.next(null);
      })
    );
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): any {
    return this.currentUserSubject.value;
  }

  forgotPassword(email: string): Observable<string> {
    return this.http.post(`${this.apiUrl}/forgot-password?email=${encodeURIComponent(email)}`, null, { responseType: 'text' });
  }

  resetPassword(email: string, otp: string, newPassword: string): Observable<string> {
    return this.http.post(`${this.apiUrl}/reset-password?email=${encodeURIComponent(email)}&otp=${encodeURIComponent(otp)}&newPassword=${encodeURIComponent(newPassword)}`, null, { responseType: 'text' });
  }

  searchUsers(query: string): Observable<any[]> {
    return this.http.get<any[]>(`http://localhost:8080/search/users?q=${encodeURIComponent(query || '')}`);
  }
  
  verifyOTP(email: string, otp: string): Observable<string> {
    return this.http.post(`${this.apiUrl}/verify-otp?email=${encodeURIComponent(email)}&otp=${encodeURIComponent(otp)}`, null, { responseType: 'text' });
  }
  
  resendVerificationEmail(email: string): Observable<string> {
    return this.http.post(`${this.apiUrl}/resend-otp?email=${encodeURIComponent(email)}`, null, { 
      responseType: 'text' 
    });
  }
  

}