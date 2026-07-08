import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { PaymentService } from '../../services/payment.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-order-success',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './order-success.component.html',
  styleUrls: ['./order-success.component.scss']
})
export class OrderSuccessComponent implements OnInit {
  orderId!: number;
  loading = true;
  success = false;

  constructor(
    private route: ActivatedRoute,
    private paymentService: PaymentService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
   
    this.route.queryParams.subscribe(params => {
      this.orderId = +params['id'];
      if (this.orderId) {
        this.confirmPayment();
      }
    });
  }

  confirmPayment(): void {

    this.paymentService.confirmPaymentInDb(this.orderId).subscribe({
      next: (res) => {
        this.loading = false;
        this.success = true;
        this.cartService.clearCart(); 
      },
      error: (err) => {
        this.loading = false;
        this.success = false;
        console.error('Error confirming payment:', err);
      }
    });
  }
}
