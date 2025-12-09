import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AvatarComponent } from '../shared/components/avatar.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, AvatarComponent],
  template: `
    <div class="profile-container">
      <!-- Profile Header -->
      <div class="profile-header">
        <div class="cover-photo"></div>
        <div class="profile-info">
          <app-avatar [src]="profile.profilePicture" [size]="'large'" class="profile-avatar"></app-avatar>
          <div class="user-details">
            <h2>{{profile.username}}</h2>
            <p class="email">{{profile.email}}</p>
            <p class="bio">{{profile.bio || 'No bio yet'}}</p>
          </div>
          <button class="edit-btn" (click)="toggleEdit()">
            {{isEditing ? '✕ Cancel' : '✏️ Edit Profile'}}
          </button>
        </div>
        
        <div class="stats">
          <div class="stat-item">
            <span class="stat-number">{{profile.postsCount}}</span>
            <span class="stat-label">Posts</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{profile.followersCount}}</span>
            <span class="stat-label">Followers</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{profile.followingCount}}</span>
            <span class="stat-label">Following</span>
          </div>
        </div>
      </div>
      
      <!-- Edit Profile Form -->
      <div *ngIf="isEditing" class="edit-form">
        <h3>Edit Profile</h3>
        <div class="form-group">
          <label>Username</label>
          <input [(ngModel)]="editProfile.username" placeholder="Username">
        </div>
        <div class="form-group">
          <label>Bio</label>
          <textarea [(ngModel)]="editProfile.bio" placeholder="Tell us about yourself..." rows="4"></textarea>
        </div>
        <div class="form-group">
          <label>Profile Picture</label>
          <input type="file" accept="image/*" (change)="onFileSelect($event)">
        </div>
        <button class="save-btn" (click)="saveProfile()">💾 Save Changes</button>
      </div>
      
      <!-- User Posts -->
      <div class="user-posts">
        <h3>My Posts</h3>
        <div class="posts-grid">
          <div *ngFor="let post of userPosts" class="post-item">
            <p>{{post.content}}</p>
            <div class="post-meta">
              <span>❤️ {{post.likes}}</span>
              <span>💬 {{post.comments}}</span>
            </div>
          </div>
        </div>
        <div *ngIf="userPosts.length === 0" class="empty-state">
          <p>No posts yet. Start sharing!</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .profile-container {
      max-width: 900px;
      margin: 0 auto;
    }
    .profile-header {
      background: linear-gradient(135deg, rgba(240, 253, 250, 0.9), rgba(255, 255, 255, 0.95));
      border: 2px solid rgba(20, 184, 166, 0.2);
      border-radius: 16px;
      overflow: hidden;
      box-shadow: 0 4px 20px rgba(20, 184, 166, 0.1);
      margin-bottom: 25px;
    }
    .cover-photo {
      height: 200px;
      background: linear-gradient(135deg, #14B8A6, #F97316);
    }
    .profile-info {
      padding: 0 30px 20px;
      position: relative;
    }
    .profile-avatar {
      margin-top: -40px;
      border: 5px solid white;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    }
    .user-details {
      margin-top: 15px;
    }
    .user-details h2 {
      margin: 0;
      color: #134E4A;
      font-size: 28px;
    }
    .email {
      color: #666;
      margin: 5px 0;
    }
    .bio {
      color: #374151;
      margin: 10px 0;
    }
    .edit-btn {
      position: absolute;
      top: 20px;
      right: 30px;
      padding: 10px 20px;
      background: linear-gradient(135deg, #14B8A6, #F97316);
      color: white;
      border: none;
      border-radius: 25px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    .edit-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 15px rgba(20, 184, 166, 0.3);
    }
    .stats {
      display: flex;
      justify-content: space-around;
      padding: 20px 30px;
      border-top: 2px solid rgba(20, 184, 166, 0.2);
    }
    .stat-item {
      text-align: center;
    }
    .stat-number {
      display: block;
      font-size: 24px;
      font-weight: 700;
      color: #14B8A6;
    }
    .stat-label {
      color: #666;
      font-size: 14px;
    }
    .edit-form {
      background: linear-gradient(135deg, rgba(240, 253, 250, 0.9), rgba(255, 255, 255, 0.95));
      border: 2px solid rgba(20, 184, 166, 0.2);
      border-radius: 16px;
      padding: 25px;
      margin-bottom: 25px;
      box-shadow: 0 4px 20px rgba(20, 184, 166, 0.1);
    }
    .edit-form h3 {
      margin: 0 0 20px 0;
      color: #134E4A;
    }
    .form-group {
      margin-bottom: 20px;
    }
    .form-group label {
      display: block;
      margin-bottom: 8px;
      font-weight: 600;
      color: #134E4A;
    }
    .form-group input, .form-group textarea {
      width: 100%;
      padding: 12px 16px;
      border: 2px solid rgba(20, 184, 166, 0.2);
      border-radius: 12px;
      font-size: 14px;
    }
    .save-btn {
      width: 100%;
      padding: 15px;
      background: linear-gradient(135deg, #14B8A6, #F97316);
      color: white;
      border: none;
      border-radius: 25px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    .save-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 15px rgba(20, 184, 166, 0.3);
    }
    .user-posts {
      background: linear-gradient(135deg, rgba(240, 253, 250, 0.9), rgba(255, 255, 255, 0.95));
      border: 2px solid rgba(20, 184, 166, 0.2);
      border-radius: 16px;
      padding: 25px;
      box-shadow: 0 4px 20px rgba(20, 184, 166, 0.1);
    }
    .user-posts h3 {
      margin: 0 0 20px 0;
      color: #134E4A;
    }
    .posts-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      gap: 20px;
    }
    .post-item {
      background: white;
      border: 2px solid rgba(20, 184, 166, 0.2);
      border-radius: 12px;
      padding: 15px;
      transition: all 0.3s ease;
    }
    .post-item:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 20px rgba(20, 184, 166, 0.2);
    }
    .post-meta {
      display: flex;
      gap: 15px;
      margin-top: 10px;
      color: #666;
      font-size: 14px;
    }
    .empty-state {
      text-align: center;
      padding: 40px;
      color: #666;
    }
  `]
})
export class ProfileComponent implements OnInit {
  profile = {
    username: 'John Doe',
    email: 'john@example.com',
    bio: 'Software Developer | Tech Enthusiast',
    profilePicture: '',
    postsCount: 24,
    followersCount: 156,
    followingCount: 89
  };

  editProfile = { ...this.profile };
  isEditing = false;
  userPosts: any[] = [];

  ngOnInit() {
    this.loadUserPosts();
  }

  toggleEdit() {
    this.isEditing = !this.isEditing;
    if (this.isEditing) {
      this.editProfile = { ...this.profile };
    }
  }

  onFileSelect(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.editProfile.profilePicture = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  saveProfile() {
    this.profile = { ...this.editProfile };
    this.isEditing = false;
  }

  loadUserPosts() {
    this.userPosts = [
      { id: 1, content: 'My first post! #excited', likes: 12, comments: 3 },
      { id: 2, content: 'Working on a new project 🚀', likes: 25, comments: 7 },
      { id: 3, content: 'Beautiful day today! ☀️', likes: 18, comments: 5 }
    ];
  }
}
