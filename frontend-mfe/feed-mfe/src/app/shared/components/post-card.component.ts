import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AvatarComponent } from './avatar.component';

@Component({
  selector: 'app-post-card',
  standalone: true,
  imports: [CommonModule, FormsModule, AvatarComponent],
  template: `
    <div class="post-card">
      <div class="post-header">
        <app-avatar [alt]="post.username"></app-avatar>
        <div class="post-info">
          <h6>{{post.username}}</h6>
          <small>{{formatDate(post.createdAt)}}</small>
        </div>
      </div>
      
      <p class="post-content" [innerHTML]="formatContent(post.content)"></p>
      
      <div *ngIf="post.media && post.media.length > 0" class="post-media">
        <img *ngFor="let media of post.media" [src]="media" class="media-image" alt="Post media">
      </div>
      
      <div class="post-actions">
        <button class="action-btn" (click)="onLike()">
          ❤️ Like ({{post.likes || 0}})
        </button>
        <button class="action-btn" (click)="toggleComments()">
          💬 Comment ({{post.comments || 0}})
        </button>
        <button class="action-btn" (click)="onShare()">
          🔗 Share
        </button>
      </div>
      
      <div *ngIf="showComments" class="comments-section">
        <div class="add-comment">
          <textarea [(ngModel)]="newComment" placeholder="Write a comment..." rows="2"></textarea>
          <button (click)="addComment()" [disabled]="!newComment.trim()">Post Comment</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .post-card {
      padding: 20px;
      border: 2px solid rgba(20, 184, 166, 0.2);
      border-radius: 16px;
      background: linear-gradient(135deg, rgba(240, 253, 250, 0.8), rgba(255, 255, 255, 0.9));
      box-shadow: 0 4px 20px rgba(20, 184, 166, 0.1);
      transition: all 0.3s ease;
      margin-bottom: 20px;
    }
    .post-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 30px rgba(20, 184, 166, 0.2);
      border-color: rgba(249, 115, 22, 0.3);
    }
    .post-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 15px;
    }
    .post-info h6 {
      margin: 0;
      font-weight: 600;
    }
    .post-info small {
      color: #666;
    }
    .post-content {
      margin: 15px 0;
      line-height: 1.6;
    }
    .post-media {
      margin: 15px 0;
    }
    .media-image {
      max-width: 100%;
      border-radius: 12px;
      margin-bottom: 10px;
    }
    .post-actions {
      display: flex;
      gap: 15px;
      padding-top: 15px;
      border-top: 1px solid rgba(20, 184, 166, 0.2);
    }
    .action-btn {
      background: linear-gradient(135deg, #14B8A6, #F97316);
      border: none;
      border-radius: 25px;
      padding: 8px 20px;
      color: white;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    .action-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 15px rgba(20, 184, 166, 0.3);
    }
    .comments-section {
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid rgba(20, 184, 166, 0.2);
    }
    .add-comment textarea {
      width: 100%;
      padding: 10px;
      border: 2px solid rgba(20, 184, 166, 0.2);
      border-radius: 8px;
      margin-bottom: 10px;
      resize: vertical;
    }
    .add-comment button {
      background: linear-gradient(135deg, #14B8A6, #F97316);
      border: none;
      border-radius: 25px;
      padding: 8px 20px;
      color: white;
      font-weight: 600;
      cursor: pointer;
    }
  `]
})
export class PostCardComponent {
  @Input() post: any;
  @Output() like = new EventEmitter<void>();
  @Output() share = new EventEmitter<void>();
  @Output() comment = new EventEmitter<string>();
  
  showComments = false;
  newComment = '';

  toggleComments() {
    this.showComments = !this.showComments;
  }

  onLike() {
    this.like.emit();
  }

  onShare() {
    this.share.emit();
  }

  addComment() {
    if (this.newComment.trim()) {
      this.comment.emit(this.newComment);
      this.newComment = '';
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }

  formatContent(content: string): string {
    if (!content) return '';
    let formatted = content.replace(/#(\w+)/g, '<span style="color: #14B8A6; font-weight: bold;">#$1</span>');
    formatted = formatted.replace(/@(\w+)/g, '<span style="color: #10B981; font-weight: bold;">@$1</span>');
    return formatted;
  }
}
