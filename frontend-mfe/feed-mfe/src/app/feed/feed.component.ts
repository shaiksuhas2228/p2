import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule, HttpClient, HttpHeaders } from '@angular/common/http';
import { FeedService, Post } from '../services/feed.service';
import { PostCardComponent } from '../shared/components/post-card.component';
import { CreatePostComponent } from '../shared/components/create-post.component';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, HttpClientModule, PostCardComponent, CreatePostComponent],
  template: `
    <div class="feed-layout">
      <div class="feed-container">
      <div class="feed-header">
        <h2 class="gradient-text">Feed</h2>
        <div class="feed-filters">
          <button class="filter-btn" [class.active]="activeFeed === 'universal'" (click)="switchFeed('universal')">
            🌍 Universal Feed
          </button>
          <button class="filter-btn" [class.active]="activeFeed === 'following'" (click)="switchFeed('following')">
            👥 Following Feed
          </button>
        </div>
      </div>
      
      <app-create-post (postCreated)="onPostCreated($event)"></app-create-post>
      
      <div *ngIf="loading" class="loading">
        <div class="spinner"></div>
        <p>Loading posts...</p>
      </div>
      
      <div *ngIf="error" class="error-message">
        {{ error }}
      </div>
      
      <div *ngIf="!loading && posts.length === 0" class="empty-state">
        <p>No posts available. Start following users to see their posts!</p>
      </div>
      
      <app-post-card 
        *ngFor="let post of posts" 
        [post]="post"
        (like)="likePost(post)"
        (share)="sharePost(post)"
        (comment)="addComment(post, $event)">
      </app-post-card>
      
        <button 
          *ngIf="!loading && posts.length > 0" 
          (click)="loadMore()" 
          class="load-more-btn">
          Load More
        </button>
      </div>
      
      <div class="suggestions-sidebar">
        <h3>Suggestions For You</h3>
        <div class="suggestions-list">
          <div *ngFor="let user of suggestions" class="suggestion-item">
            <div class="user-avatar">{{user.username[0]}}</div>
            <div class="user-info">
              <h4>{{user.username}}</h4>
              <p>{{user.bio}}</p>
            </div>
            <button class="follow-btn" (click)="followUser(user)">➕</button>
          </div>
          <div *ngIf="suggestions.length === 0" class="no-suggestions">
            <p>No suggestions</p>
          </div>
        </div>
        
        <div *ngIf="following.length > 0" class="following-section">
          <h3>Following</h3>
          <div class="following-list">
            <div *ngFor="let user of following" class="following-item">
              <div class="user-avatar">{{user.username[0]}}</div>
              <div class="user-info">
                <h4>{{user.username}}</h4>
              </div>
              <button class="unfollow-btn" (click)="unfollowUser(user)">✖</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .feed-layout {
      display: flex;
      gap: 25px;
      max-width: 1400px;
      margin: 0 auto;
    }
    .feed-container {
      flex: 1;
      padding: 25px;
      background: linear-gradient(135deg, rgba(240, 253, 250, 0.3), rgba(255, 255, 255, 0.5));
      border-radius: 20px;
    }
    .suggestions-sidebar {
      width: 320px;
      flex-shrink: 0;
      background: linear-gradient(135deg, rgba(240, 253, 250, 0.9), rgba(255, 255, 255, 0.95));
      border: 2px solid rgba(20, 184, 166, 0.2);
      border-radius: 16px;
      padding: 20px;
      height: fit-content;
      position: sticky;
      top: 100px;
    }
    .suggestions-sidebar h3 {
      margin: 0 0 15px 0;
      color: #134E4A;
      font-size: 16px;
    }
    .suggestions-list, .following-list {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
    .suggestion-item, .following-item {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px;
      background: white;
      border-radius: 10px;
      border: 2px solid rgba(20, 184, 166, 0.1);
    }
    .user-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background: linear-gradient(135deg, #14B8A6, #F97316);
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 700;
      font-size: 16px;
    }
    .user-info {
      flex: 1;
      min-width: 0;
    }
    .user-info h4 {
      margin: 0;
      font-size: 14px;
      color: #134E4A;
    }
    .user-info p {
      margin: 2px 0 0 0;
      font-size: 11px;
      color: #666;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    .follow-btn {
      padding: 6px 12px;
      background: linear-gradient(135deg, #14B8A6, #F97316);
      color: white;
      border: none;
      border-radius: 15px;
      font-size: 14px;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    .follow-btn:hover {
      transform: scale(1.05);
    }
    .unfollow-btn {
      padding: 4px 8px;
      background: rgba(239, 68, 68, 0.1);
      color: #EF4444;
      border: 2px solid #EF4444;
      border-radius: 50%;
      font-size: 10px;
      cursor: pointer;
    }
    .unfollow-btn:hover {
      background: #EF4444;
      color: white;
    }
    .no-suggestions {
      text-align: center;
      padding: 15px;
      color: #666;
      font-size: 13px;
    }
    .following-section {
      margin-top: 20px;
      padding-top: 15px;
      border-top: 2px solid rgba(20, 184, 166, 0.2);
    }
    .feed-header {
      margin-bottom: 30px;
    }
    .feed-header h2 {
      margin-bottom: 20px;
      color: #134E4A;
    }
    .feed-filters {
      display: flex;
      gap: 15px;
      justify-content: center;
    }
    .filter-btn {
      padding: 12px 24px;
      border: 2px solid rgba(20, 184, 166, 0.3);
      border-radius: 25px;
      background: white;
      color: #134E4A;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    .filter-btn:hover {
      background: rgba(20, 184, 166, 0.1);
      transform: translateY(-2px);
    }
    .filter-btn.active {
      background: linear-gradient(135deg, #14B8A6, #F97316);
      color: white;
      border-color: transparent;
      box-shadow: 0 4px 15px rgba(20, 184, 166, 0.3);
    }
    .loading {
      text-align: center;
      padding: 3rem;
    }
    .spinner {
      border: 4px solid rgba(20, 184, 166, 0.2);
      border-top: 4px solid #14B8A6;
      border-radius: 50%;
      width: 50px;
      height: 50px;
      animation: spin 1s linear infinite;
      margin: 0 auto 20px;
    }
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
    .error-message {
      color: #DC2626;
      padding: 1rem;
      background: #FEE2E2;
      border-radius: 8px;
      margin-bottom: 20px;
    }
    .empty-state {
      text-align: center;
      padding: 3rem;
      color: #666;
    }
    .load-more-btn {
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
    .load-more-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 15px rgba(20, 184, 166, 0.3);
    }
  `]
})
export class FeedComponent implements OnInit {
  posts: Post[] = [];
  loading = false;
  error = '';
  currentPage = 0;
  activeFeed = 'universal';
  suggestions: any[] = [];
  following: any[] = [];

  constructor(private feedService: FeedService, private http: HttpClient) {}

  ngOnInit(): void {
    this.loadFeed();
    this.loadSuggestions();
  }

  loadSuggestions(): void {
    const token = localStorage.getItem('revhub_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    this.http.get<any[]>('http://localhost:8081/api/users/suggestions', { headers }).subscribe({
      next: (users) => {
        this.suggestions = users.map(u => ({
          id: u.id,
          username: u.username,
          bio: u.bio || 'New user'
        }));
      },
      error: () => this.suggestions = []
    });
  }

  followUser(user: any): void {
    this.following.push(user);
    this.suggestions = this.suggestions.filter(u => u.id !== user.id);
  }

  unfollowUser(user: any): void {
    this.following = this.following.filter(u => u.id !== user.id);
    this.suggestions.push(user);
  }

  switchFeed(feedType: string): void {
    this.activeFeed = feedType;
    this.currentPage = 0;
    this.posts = [];
    this.loadFeed();
  }

  loadFeed(): void {
    this.loading = true;
    this.error = '';
    
    this.feedService.getFeed(this.currentPage).subscribe({
      next: (response) => {
        this.posts = [...this.posts, ...response.posts];
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load feed. Using demo data for UI preview.';
        this.loading = false;
        this.loadDemoData();
        console.error('Feed error:', err);
      }
    });
  }

  loadDemoData(): void {
    this.posts = [
      {
        id: 1,
        username: 'John Doe',
        content: 'Just finished an amazing project! #coding #webdev',
        userId: 1,
        createdAt: new Date().toISOString(),
        likes: 15,
        comments: 3,
        media: []
      },
      {
        id: 2,
        username: 'Jane Smith',
        content: 'Beautiful sunset today! 🌅 #nature #photography',
        userId: 2,
        createdAt: new Date(Date.now() - 3600000).toISOString(),
        likes: 42,
        comments: 8,
        media: []
      },
      {
        id: 3,
        username: 'Mike Johnson',
        content: 'Check out my new blog post about @angular and micro-frontends! #angular #microfrontends',
        userId: 3,
        createdAt: new Date(Date.now() - 7200000).toISOString(),
        likes: 28,
        comments: 12,
        media: []
      }
    ];
  }

  loadMore(): void {
    this.currentPage++;
    this.loadFeed();
  }

  likePost(post: Post): void {
    console.log('Liked post:', post.id);
  }

  sharePost(post: Post): void {
    console.log('Shared post:', post.id);
  }

  addComment(post: Post, content: string): void {
    console.log('Added comment to post:', post.id, content);
  }

  onPostCreated(postData: any): void {
    const newPost: Post = {
      id: Date.now(),
      username: 'Current User',
      content: postData.content,
      userId: 1,
      createdAt: new Date().toISOString(),
      likes: 0,
      comments: 0,
      media: []
    };
    this.posts = [newPost, ...this.posts];
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }
}
