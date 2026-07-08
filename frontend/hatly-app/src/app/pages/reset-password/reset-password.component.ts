import { Component,OnInit } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router,RouterLink} from '@angular/router';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [ReactiveFormsModule,RouterLink,CommonModule],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.scss'
})


export class ResetPasswordComponent implements OnInit
{
  resetpassForm!:FormGroup
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  constructor(private authservice:AuthService,private router:Router,private route: ActivatedRoute){}

ngOnInit(): void {
  
    const emailFromUrl = this.route.snapshot.queryParamMap.get('email') || '';

    this.resetpassForm = new FormGroup({
      email: new FormControl(emailFromUrl, [Validators.required, Validators.email]),
      otp: new FormControl('', [Validators.required, Validators.minLength(4)]),
      newPassword: new FormControl('', [Validators.required, Validators.minLength(6)])
    });
  }
onSubmit(): void {
    if (this.resetpassForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      this.authservice.resetpassword(this.resetpassForm.value).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.successMessage = 'Password reset successfully! Redirecting to login...';
          
       
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 3000);
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = 'Invalid or expired OTP code. Please try again.';
        }
      });
    }
  }
}
