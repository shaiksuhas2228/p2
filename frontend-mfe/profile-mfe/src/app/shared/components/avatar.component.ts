import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-avatar',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="avatar" [class.avatar-large]="size === 'large'" [class.avatar-small]="size === 'small'" [class.avatar-online]="online">
      <img [src]="src || 'https://via.placeholder.com/40'" 
           class="avatar-image" 
           [alt]="alt"
           [style.width.px]="getSize()"
           [style.height.px]="getSize()">
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
export class AvatarComponent {
  @Input() src?: string;
  @Input() alt = 'Avatar';
  @Input() size: 'small' | 'medium' | 'large' = 'medium';
  @Input() online = false;

  getSize(): number {
    switch(this.size) {
      case 'small': return 30;
      case 'large': return 80;
      default: return 40;
    }
  }
}
