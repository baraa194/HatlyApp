import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { ForgetPasswordComponent } from './pages/forget-password/forget-password.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { RestaurantComponent } from './pages/restaurant/restaurant.component';
import { BranchCategoryProductsComponent } from './pages/branch-category-products/branch-category-products.component';
import { CartComponent } from './pages/cart/cart.component';
import { DeliveryAgentComponent } from './pages/delivery-agent/delivery-agent.component';
import { OrderTrackingComponent } from './pages/order-tracking/order-tracking.component';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
  
  { path: 'login', component: LoginComponent,title:'login' },
  { path: 'register', component: RegisterComponent,title:'register' },
  {path:'forget-password',component:ForgetPasswordComponent,title:'forget-password' },
    {path:'reset-password',component:ResetPasswordComponent,title:'reset-password' },
    { path: 'cart', component: CartComponent },
 { path: 'restaurant/:restaurantId', component: RestaurantComponent },
 { 
    path: 'branches/:branchId/categories/:categoryName/products', 
    component: BranchCategoryProductsComponent
  },
  {
    path: 'order-success',
    loadComponent: () => import('./pages/order-success/order-success.component').then(m => m.OrderSuccessComponent)
  },
  {path:'delivery',component:DeliveryAgentComponent},
  { 
    path: 'orders/:id/tracking', 
    component: OrderTrackingComponent
  },

  {path:'home',component:HomeComponent,title:'home'}

];
