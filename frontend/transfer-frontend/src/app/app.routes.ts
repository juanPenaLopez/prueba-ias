import { Routes } from '@angular/router';
import { TransfersPageComponent } from './features/transfers/pages/transfers-page/transfers-page.component';

export const routes: Routes = [
  { path: '', component: TransfersPageComponent },
  { path: '**', redirectTo: '' }
];
