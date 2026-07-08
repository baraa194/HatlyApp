import { Component, OnInit } from '@angular/core';
import { ProductBranchResponse, RestaurantService } from '../../services/restaurant.service';
import { ActivatedRoute } from '@angular/router';
import { CommonModule, CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-branch-category-products',
  standalone: true,
  imports: [CurrencyPipe,CommonModule],
  templateUrl: './branch-category-products.component.html',
  styleUrl: './branch-category-products.component.scss'
})

export class BranchCategoryProductsComponent implements OnInit{
  products: ProductBranchResponse[] = [];
  branchId!: number;
  categoryName!: string;
  isLoading: boolean = false;
  errorMessage: string = '';
  constructor(
    private route: ActivatedRoute,
    private restaurantService: RestaurantService
  ) {}
ngOnInit(): void {
 
    this.branchId = Number(this.route.snapshot.paramMap.get('branchId'));
    this.categoryName = this.route.snapshot.paramMap.get('categoryName') || '';

   
    if (this.branchId && this.categoryName) {
      this.loadProducts();
    }
  }
  loadProducts(): void {
    this.isLoading = true;
    this.restaurantService.getBranchCategories(this.branchId, this.categoryName).subscribe({
      next: (data) => {
        this.products = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'error while loading products'
        this.isLoading = false;
        console.error(err);
      }
    });
  }

}
