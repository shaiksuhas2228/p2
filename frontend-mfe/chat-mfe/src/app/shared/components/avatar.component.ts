import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-avatar',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="avatar" [class.avatar-large]="size === 'large'" [class.avatar-small]="size === 'small'" [class.avatar-online]="online">
      <img *ngIf="hasValidImage" 
           [src]="src" 
           class="avatar-image" 
           [alt]="alt"
           [style.width.px]="getSize()"
           [style.height.px]="getSize()"
           (error)="onImageError()">
      <div *ngIf="!hasValidImage" 
           class="avatar-placeholder"
           [style.width.px]="getSize()"
           [style.height.px]="getSize()"
           [style.background]="getAvatarColor()">
        {{ getInitial() }}
      </div>
    </div>
  `,
  styles: [`
    .avatar {
      display: inline-block;
      position: relative;
      transition: all 0.3s ease;
    }
    .avatar:hover {
      transform: scale(1.05);
    }
    .avatar-image {
      border-radius: 50%;
      object-fit: cover;
      border: 2px solid rgba(20, 184, 166, 0.3);
      transition: all 0.3s ease;
    }
    .avatar-image:hover {
      border-color: #F97316;
      box-shadow: 0 4px 15px rgba(20, 184, 166, 0.2);
    }
    .avatar-placeholder {
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-weight: 600;
      font-size: 16px;
      border: 2px solid rgba(20, 184, 166, 0.3);
      transition: all 0.3s ease;
      text-transform: uppercase;
    }
    .avatar-placeholder:hover {
      border-color: #F97316;
      box-shadow: 0 4px 15px rgba(20, 184, 166, 0.2);
    }
    .avatar-online::after {
      content: '';
      position: absolute;
      bottom: 2px;
      right: 2px;
      width: 12px;
      height: 12px;
      background: #10B981;
      border: 2px solid white;
      border-radius: 50%;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
  `]
})
export class AvatarComponent implements OnInit {
  @Input() src?: string;
  @Input() name?: string;
  @Input() alt = 'Avatar';
  @Input() size: 'small' | 'medium' | 'large' = 'medium';
  @Input() online = false;

  hasValidImage = false;

  ngOnInit() {
    this.checkImage();
  }

  checkImage() {
    if (this.src && this.src.trim() !== '') {
      this.hasValidImage = true;
    } else {
      this.hasValidImage = false;
    }
  }

  onImageError() {
    this.hasValidImage = false;
  }

  getInitial(): string {
    if (this.name && this.name.trim() !== '') {
      return this.name.charAt(0).toUpperCase();
    }
    return this.alt.charAt(0).toUpperCase();
  }

  getAvatarColor(): string {
    const colors = [
      'linear-gradient(135deg, #14B8A6, #0D9488)',
      'linear-gradient(135deg, #F97316, #EA580C)',
      'linear-gradient(135deg, #8B5CF6, #7C3AED)',
      'linear-gradient(135deg, #EC4899, #DB2777)',
      'linear-gradient(135deg, #3B82F6, #2563EB)',
      'linear-gradient(135deg, #10B981, #059669)',
      'linear-gradient(135deg, #F59E0B, #D97706)',
      'linear-gradient(135deg, #6366F1, #4F46E5)'
    ];
    
    const name = this.name || this.alt;
    const charCode = name.charCodeAt(0);
    return colors[charCode % colors.length];
  }

  getSize(): number {
    switch(this.size) {
      case 'small': return 30;
      case 'large': return 80;
      default: return 40;
    }
  }
}
