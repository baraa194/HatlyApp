import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RestaurantResponse {
  id: number;
  ownerId: number;
  name: string;
  logoUrl: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface BranchResponse {
  id: number;
  addressDetailed: string;
  lat: number;
  lng: number;
  deliveryRadius: number;
  currency: string;
  openAt: string;        
  closeAt: string;      
  acceptOrders: boolean;
  label: string;        
  commission: number;
  isActive: boolean;  
  restaurant_id: number;
}
export interface ProductBranchResponse {
  id: number;
  name: string;
  description: string;
  imgUrl: string;       
  restaurantId: number;
  categoryId: number;
  categoryName: string;
  isAvailable: boolean;
  price: number;      
  stock: number;
}
export interface RestaurntCategory {
  id: number;
  restaurant_id: number;
  name: string;
  created_at: string | Date;
  updated_at: string | Date;
}

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {

  private baseUrl = 'http://localhost:8080/restaurants'; 
  private branchUrl = 'http://localhost:8080/branch';
  private productsUrl = 'http://localhost:8080/branches';
    private productcatsUrl = 'http://localhost:8080/products';
  

  constructor(private http: HttpClient) { }

  getAllRestaurants(): Observable<RestaurantResponse[]> {
    return this.http.get<RestaurantResponse[]>(this.baseUrl) ;
  }
  getAllCategories(): Observable<string[]> { 
  return this.http.get<string[]>('http://localhost:8080/categories/all');
}
getNearestBranch(restaurantId: number, lat: number, lng: number): Observable<BranchResponse> {
    return this.http.get<any>(`${this.branchUrl}/${restaurantId}/nearest-branch?lat=${lat}&lng=${lng}`);
  }

  getProductsByBranchId(branchId: number): Observable<ProductBranchResponse[]> {
    return this.http.get<ProductBranchResponse[]>(`${this.productsUrl}/${branchId}/products`);
  }
  getRestaurentCategories(restaurantId:number):Observable<RestaurntCategory[]>
  {
        return this.http.get<RestaurntCategory[]>(`${this.productcatsUrl}/restaurant/${restaurantId}/categories`);
  }

  getBranchCategories(branchId:number,categoryName:string):Observable<ProductBranchResponse[]>
  {
    return this.http.get<ProductBranchResponse[]>(`${this.productsUrl}/${branchId}/categories/${categoryName}/products`)
  }
}