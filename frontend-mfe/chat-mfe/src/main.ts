import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { ChatModule } from './app/chat/chat.module';

platformBrowserDynamic().bootstrapModule(ChatModule)
  .catch(err => console.error(err));
