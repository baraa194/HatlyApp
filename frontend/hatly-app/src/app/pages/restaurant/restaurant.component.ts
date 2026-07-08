import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { BranchResponse, ProductBranchResponse, RestaurantService, RestaurntCategory } from '../../services/restaurant.service'; 
import { CustomerAddress } from '../../services/address.service';
import { CartService } from '../../services/cart.service';



@Component({
  selector: 'app-restaurant',
   standalone: true,
  imports: [RouterLink],
  templateUrl: './restaurant.component.html',
  styleUrl: './restaurant.component.scss'
})
export class RestaurantComponent implements OnInit {
  restaurantId!: number;
    categoryName!: string;
 nearestBranchData:BranchResponse | null = null;
  errorMessage: string = '';
  isLoading: boolean = true;
  productsList: ProductBranchResponse[] = [];
  categoriesList: RestaurntCategory[] = [];
  branchId!: number;
  cartCount: number = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private restaurantService: RestaurantService,
    private cartService: CartService
  ) {
    
    const navigation = this.router.getCurrentNavigation();
    const stateAddress = navigation?.extras.state?.['address'] as CustomerAddress;

   
    if (stateAddress) {
   
      this.restaurantId = Number(this.route.snapshot.paramMap.get('restaurantId'));
      
  
      this.checkBranchAvailability(this.restaurantId, stateAddress.lat, stateAddress.lng);
      this.cartService.cart$.subscribe(items => {
      this.cartCount = items.reduce((sum, item) => sum + item.quantity, 0);
    });
      this.loadRestaurantCategories(this.restaurantId);
    } else {
 
      this.router.navigate(['/home']);
    }
  }

  ngOnInit(): void {}

  checkBranchAvailability(restaurantId: number, lat: number, lng: number): void {
    this.restaurantService.getNearestBranch(restaurantId, lat, lng).subscribe({
      next: (branchResponse) => {
        this.nearestBranchData = branchResponse; 
        this.isLoading = false;
          localStorage.setItem('nearest_branch', JSON.stringify(branchResponse));
        console.log('Nearest branch loaded successfully:', branchResponse);
     
           this.loadBranchProducts(branchResponse.id);
          
      },
      error: (err) => {
       
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Sorry, this restaurant does not deliver to your current location.';
        console.error('Delivery Error:', err);
      }
    });
  }
  loadBranchProducts(branchId: number): void {
    this.branchId = branchId;
  
    this.restaurantService.getProductsByBranchId(branchId).subscribe({
      next: (products: ProductBranchResponse[]) => {
        this.productsList = products;
        this.isLoading = false;
      
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error loading products:', err);
      }
    });
  }

loadRestaurantCategories(restaurantId: number): void {
    this.restaurantService.getRestaurentCategories(restaurantId).subscribe({
      next: (categories: RestaurntCategory[]) => {
        this.categoriesList = categories;
        console.log('Categories loaded successfully:', categories);
      },
      error: (err) => {
        console.error('Error loading categories:', err);
      }
    });
  }
  selectedCategory: string = 'All'; 
  loadProducts(): void {
    this.isLoading = true;
    this.restaurantService.getBranchCategories(this.branchId, this.categoryName).subscribe({
      next: (data) => {
        this.productsList = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'error while loading products'
        this.isLoading = false;
        console.error(err);
      }
    });
  }

onCategorySelect(categoryName: string): void {
  this.selectedCategory = categoryName;
  this.isLoading = true;


  this.restaurantService.getBranchCategories(this.branchId, categoryName).subscribe({
    next: (products: ProductBranchResponse[]) => {
      this.productsList = products; 
      this.isLoading = false;
    },
    error: (err) => {
      this.isLoading = false;
      this.productsList = [];
      console.error('Error loading category products:', err);
    }
  });
}


resetToAll(): void {
  this.selectedCategory = 'All';
  this.isLoading = true;
  this.loadBranchProducts(this.branchId); 
}

addToCart(product: ProductBranchResponse): void {
this.cartService.addToCart(product);

    
    
  }
}
