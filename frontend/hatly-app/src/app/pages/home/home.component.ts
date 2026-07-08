import { CommonModule } from '@angular/common';
import { Component,OnInit } from '@angular/core';
import { RestaurantService, RestaurantResponse } from '../../services/restaurant.service';
import { AddressService, CustomerAddress } from '../../services/address.service'
import { RouterLink,Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule,RouterLink,FormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {

  restaurantsList: RestaurantResponse[] = [];
  categoriesList: string[] = []; 
  isLoading: boolean = true;
  errorMessage: string = '';
  addressesList: CustomerAddress[] = [];
  currentAddress: CustomerAddress | null = null;
  userId!:number

  constructor(private restaurantService: RestaurantService,private addressService:AddressService,
    private authService:AuthService,private router:Router
  ) {}

  ngOnInit(): void {
    const loggedInUserId = this.authService.getCurrentUserId();
    
    if (loggedInUserId) {
      this.userId = loggedInUserId;
      this.loadHomeData();
    } else {
   
       this.router.navigate(['/login']);
    }
    this.loadHomeData();

  }

  loadHomeData(): void {

    this.restaurantService.getAllCategories().subscribe({
      next: (cats) => {
        this.categoriesList = cats;
      },
      error: (err) => console.error('Error loading categories:', err)
    });

   
    this.restaurantService.getAllRestaurants().subscribe({
      next: (data) => {
        this.restaurantsList = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load restaurants.';
        this.isLoading = false;
      }
    });

    this.addressService.getAllAddresses(this.userId).subscribe({
       next:(res)=>{
        this.addressesList=res
       },
       error:(err) =>console.error('Error loading all addresses:', err)
           
      
    })
    this.addressService.getDefaultAddress(this.userId).subscribe({
    next: (defaultAddress) => {
      this.currentAddress = defaultAddress;
  
      
    
      localStorage.setItem('selected_address', JSON.stringify(defaultAddress));
    },
    error: (err) => console.error('Error loading default address:', err)
  });



  }
    onAddressChange(address:CustomerAddress):void{
    this.currentAddress = address;
    console.log('User changed his active address to:', address.label);
  }
  searchTerm: string = '';

get filteredRestaurants() {
  if (!this.searchTerm.trim()) {
    return this.restaurantsList;
  }
  const term = this.searchTerm.toLowerCase();
  return this.restaurantsList.filter(r => r.name.toLowerCase().includes(term));
}
}
