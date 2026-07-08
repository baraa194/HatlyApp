import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { OrderService,OrderDeliveryResponse  } from '../../services/order.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-order-tracking',
  templateUrl: './order-tracking.component.html',
  styleUrls: ['./order-tracking.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class OrderTrackingComponent implements OnInit, OnDestroy {
  orderId!: number;
  trackingData: OrderDeliveryResponse | null = null;
  pollingInterval: any;
  loading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService,
    private sanitizer: DomSanitizer
  ) {}
    get mapUrl(): SafeResourceUrl {
    if (!this.trackingData?.lat || !this.trackingData?.lng) {
      return '';
    }
    const url = `https://maps.google.com/maps?q=${this.trackingData.lat},${this.trackingData.lng}&t=&z=15&ie=UTF8&iwloc=&output=embed`;
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

  ngOnInit(): void {
  
    this.orderId = +this.route.snapshot.paramMap.get('id')!;
    

    this.fetchTrackingDetails();


    this.pollingInterval = setInterval(() => {
      this.fetchTrackingDetails();
    }, 15000);
  }

  fetchTrackingDetails(): void {
    this.orderService.getOrderTracking(this.orderId).subscribe({
      next: (data) => {
        this.trackingData = data;
        this.loading = false;
        console.log('🔄 Tracking Data Updated:', data);
      },
      error: (err) => {
        console.error('Failed to load tracking data', err);
        this.loading = false;
      }
    });
  }
  statusSteps = [
  { key: 'PENDING',             icon: '✓',  label: 'Pending' },
  { key: 'PREPARING',           icon: '👨‍🍳', label: 'Preparing' },
  { key: 'READY_FOR_PICKUP',    icon: '📦', label: 'Ready' },
  { key: 'OUT_FOR_DELIVERY',    icon: '🛵', label: 'On the way' },
  { key: 'DELIVERED',           icon: '🏠', label: 'Delivered' },
];

private statusOrder = this.statusSteps.map(s => s.key);

isStepCompleted(step: string): boolean {
  const currentIndex = this.statusOrder.indexOf(this.trackingData?.orderStatus!);
  const stepIndex = this.statusOrder.indexOf(step);
  return currentIndex > stepIndex;
}

isStepActive(step: string): boolean {
  return this.trackingData?.orderStatus === step;
}

  ngOnDestroy(): void {

    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  }
}
