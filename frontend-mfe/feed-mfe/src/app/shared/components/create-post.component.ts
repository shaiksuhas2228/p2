import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="create-post-card">
      <h3>Create Post</h3>
      <textarea 
        [(ngModel)]="content" 
        placeholder="What's on your mind?"
        rows="4"
        class="post-textarea"></textarea>
      
      <div class="post-options">
        <button class="option-btn" (click)="photoInput.click()">
          📷 Photo
        </button>
        <input #photoInput type="file" accept="image/*" (change)="onFileSelect($event)" style="display: none;">
        
        <button class="option-btn" (click)="videoInput.click()">
          🎥 Video
        </button>
        <input #videoInput type="file" accept="video/*" (change)="onFileSelect($event)" style="display: none;">
        
        <button class="option-btn" (click)="addLocation()">
          📍 Location
        </button>
        
        <select [(ngModel)]="visibility" class="visibility-select">
          <option value="public">🌍 Public</option>
          <option value="followers">👥 Followers Only</option>
        </select>
      </div>
      
      <div *ngIf="location" class="location-tag">
        📍 {{location}}
        <button class="remove-btn" (click)="removeLocation()">✕</button>
      </div>
      
      <div *ngIf="selectedFile" class="file-preview">
        <img *ngIf="previewUrl && !isVideo" [src]="previewUrl" alt="Preview">
        <video *ngIf="previewUrl && isVideo" [src]="previewUrl" controls></video>
        <button class="remove-btn" (click)="removeFile()">✕</button>
      </div>
      
      <button 
        class="post-btn" 
        (click)="createPost()" 
        [disabled]="!content.trim()">
        Post
      </button>
    </div>
  `,
  styles: [`
    .create-post-card {
      background: linear-gradient(135deg, rgba(240, 253, 250, 0.9), rgba(255, 255, 255, 0.95));
      border: 2px solid rgba(20, 184, 166, 0.2);
      border-radius: 16px;
      padding: 25px;
      margin-bottom: 25px;
      box-shadow: 0 4px 20px rgba(20, 184, 166, 0.1);
    }
    h3 {
      margin: 0 0 20px 0;
      color: #134E4A;
      font-size: 20px;
    }
    .post-textarea {
      width: 100%;
      padding: 15px;
      border: 2px solid rgba(20, 184, 166, 0.2);
      border-radius: 12px;
      font-size: 16px;
      resize: vertical;
      margin-bottom: 15px;
      transition: all 0.3s ease;
    }
    .post-textarea:focus {
      outline: none;
      border-color: #14B8A6;
      box-shadow: 0 0 0 3px rgba(20, 184, 166, 0.1);
    }
    .post-options {
      display: flex;
      gap: 15px;
      margin-bottom: 15px;
      flex-wrap: wrap;
    }
    .option-btn {
      padding: 10px 20px;
      background: rgba(20, 184, 166, 0.1);
      border: 2px solid #14B8A6;
      border-radius: 25px;
      color: #14B8A6;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    .option-btn:hover {
      background: #14B8A6;
      color: white;
    }
    .visibility-select {
      padding: 10px 20px;
      border: 2px solid rgba(20, 184, 166, 0.2);
      border-radius: 25px;
      background: white;
      font-weight: 600;
      cursor: pointer;
    }
    .file-preview {
      position: relative;
      margin-bottom: 15px;
    }
    .file-preview img, .file-preview video {
      max-width: 100%;
      max-height: 300px;
      border-radius: 12px;
    }
    .remove-btn {
      position: absolute;
      top: 10px;
      right: 10px;
      background: rgba(239, 68, 68, 0.9);
      color: white;
      border: none;
      border-radius: 50%;
      width: 30px;
      height: 30px;
      cursor: pointer;
      font-size: 18px;
    }
    .post-btn {
      width: 100%;
      padding: 15px;
      background: linear-gradient(135deg, #14B8A6, #F97316);
      color: white;
      border: none;
      border-radius: 25px;
      font-weight: 600;
      font-size: 16px;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    .post-btn:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 6px 15px rgba(20, 184, 166, 0.3);
    }
    .post-btn:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
    .location-tag {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px 15px;
      background: rgba(20, 184, 166, 0.1);
      border: 2px solid rgba(20, 184, 166, 0.3);
      border-radius: 8px;
      margin-bottom: 15px;
      color: #134E4A;
      font-weight: 600;
    }
    .location-tag .remove-btn {
      background: rgba(239, 68, 68, 0.9);
      color: white;
      border: none;
      border-radius: 50%;
      width: 20px;
      height: 20px;
      cursor: pointer;
      font-size: 12px;
      margin-left: auto;
    }
  `]
})
export class CreatePostComponent {
  @Output() postCreated = new EventEmitter<any>();
  
  content = '';
  visibility = 'public';
  selectedFile: File | null = null;
  previewUrl: string | null = null;
  isVideo = false;
  location = '';

  onFileSelect(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      this.isVideo = file.type.startsWith('video/');
      
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.previewUrl = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  removeFile() {
    this.selectedFile = null;
    this.previewUrl = null;
    this.isVideo = false;
  }

  addLocation() {
    this.location = prompt('Enter location:') || '';
  }

  removeLocation() {
    this.location = '';
  }

  createPost() {
    if (this.content.trim()) {
      this.postCreated.emit({
        content: this.content,
        visibility: this.visibility,
        file: this.selectedFile,
        location: this.location
      });
      this.content = '';
      this.visibility = 'public';
      this.location = '';
      this.removeFile();
    }
  }
}
