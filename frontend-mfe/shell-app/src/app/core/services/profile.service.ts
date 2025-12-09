import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post } from './post.service';

export interface User {
  id: number;
  username: string;
  email: string;
  profilePicture?: string;
  bio?: string;
  isPrivate?: boolean;
  createdDate: string;
  followersCount?: number;
  followingCount?: number;
  followStatus?: string;
}

export interface FollowRequest {
  id: number;
  follower: User;
  following: User;
  status: 'PENDING' | 'ACCEPTED';
  createdDate: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private userApiUrl = 'http://localhost:8081/api/users';
  private followApiUrl = 'http://localhost:8084/api/follow';

  constructor(private http: HttpClient) { }

  getProfile(username: string): Observable<User> {
    return this.http.get<User>(`${this.userApiUrl}/${username}`);
  }

  getUserPosts(username: string): Observable<Post[]> {
    return this.http.get<Post[]>(`http://localhost:8082/api/posts/user/${username}`);
  }

  updateProfile(updates: { bio?: string; profilePicture?: string; isPrivate?: string }): Observable<User> {
    return this.http.put<User>(`${this.userApiUrl}/profile`, updates);
  }
  
  updateProfileWithFile(formData: FormData): Observable<User> {
    const profileData: any = {};
    
    // If no file, just send other data
    if (!formData.has('profilePicture') || !(formData.get('profilePicture') instanceof File)) {
      formData.forEach((value, key) => {
        profileData[key] = value;
      });
      return this.http.put<User>(`${this.userApiUrl}/profile`, profileData);
    }
    
    // Handle file upload with base64 conversion
    return new Observable<User>(observer => {
      const file = formData.get('profilePicture') as File;
      const reader = new FileReader();
      
      formData.forEach((value, key) => {
        if (key !== 'profilePicture') {
          profileData[key] = value;
        }
      });
      
      reader.onload = () => {
        profileData.profilePicture = reader.result as string;
        this.http.put<User>(`${this.userApiUrl}/profile`, profileData).subscribe({
          next: result => {
            observer.next(result);
            observer.complete();
          },
          error: err => observer.error(err)
        });
      };
      reader.onerror = () => {
        observer.error(new Error('Failed to read file'));
      };
      reader.readAsDataURL(file);
    });
  }
  
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.userApiUrl}/all`);
  }

  followUser(username: string): Observable<{message: string}> {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      throw new Error('User not authenticated');
    }
    try {
      const currentUser = JSON.parse(userStr);
      if (!currentUser.id) {
        throw new Error('User not authenticated');
      }
      const headers = new HttpHeaders({ 'X-User-Id': currentUser.id.toString() });
      return this.http.post<{message: string}>(`${this.followApiUrl}/${username}`, {}, { headers });
    } catch (e) {
      throw new Error('Invalid user data');
    }
  }

  unfollowUser(username: string): Observable<{message: string}> {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      throw new Error('User not authenticated');
    }
    try {
      const currentUser = JSON.parse(userStr);
      const headers = new HttpHeaders({ 'X-User-Id': currentUser.id.toString() });
      return this.http.delete<{message: string}>(`${this.followApiUrl}/${username}`, { headers });
    } catch (e) {
      throw new Error('Invalid user data');
    }
  }

  getFollowStatus(username: string): Observable<{status: string}> {
    return this.http.get<{status: string}>(`${this.followApiUrl}/status/${username}`);
  }

  getPendingFollowRequests(): Observable<FollowRequest[]> {
    return this.http.get<FollowRequest[]>(`${this.followApiUrl}/requests`);
  }

  acceptFollowRequest(followId: number): Observable<{message: string}> {
    return this.http.post<{message: string}>(`${this.followApiUrl}/requests/${followId}/accept`, {});
  }

  rejectFollowRequest(followId: number): Observable<{message: string}> {
    return this.http.post<{message: string}>(`${this.followApiUrl}/requests/${followId}/reject`, {});
  }

  cancelFollowRequest(username: string): Observable<{message: string}> {
    return this.http.delete<{message: string}>(`${this.followApiUrl}/cancel/${username}`);
  }

  getFollowers(username: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.followApiUrl}/${username}/followers`);
  }

  getFollowing(username: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.followApiUrl}/${username}/following`);
  }
  
  removeFollower(username: string): Observable<{message: string}> {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      throw new Error('User not authenticated');
    }
    try {
      const currentUser = JSON.parse(userStr);
      const headers = new HttpHeaders({ 'X-User-Id': currentUser.id.toString() });
      return this.http.delete<{message: string}>(`${this.followApiUrl}/remove/${username}`, { headers });
    } catch (e) {
      throw new Error('Invalid user data');
    }
  }
  
  searchUsers(query: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.userApiUrl}/search?query=${encodeURIComponent(query)}`);
  }
}