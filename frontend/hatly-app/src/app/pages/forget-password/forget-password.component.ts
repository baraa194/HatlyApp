import { Component, OnInit } from '@angular/core';
import { RouterLink,Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormGroup ,ReactiveFormsModule, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-forget-password',
  standalone: true,
  imports: [RouterLink,ReactiveFormsModule],
  templateUrl: './forget-password.component.html',
  styleUrl: './forget-password.component.scss'
})
export class ForgetPasswordComponent implements OnInit {

  forgetpassForm!:FormGroup
  errorMessage: string = '';
  isLoading: boolean = false;
  constructor(private authservice:AuthService,private router:Router)
  {}
 
  ngOnInit(): void {
        this. forgetpassForm=new FormGroup({
             email:new FormControl('',[Validators.required,Validators.email]),
      
      })   
  }
  onSubmit(): void {
    if (this.forgetpassForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      this.authservice.forgotpassword(this.forgetpassForm.value).subscribe({
        next: (response) => {
          this.isLoading = false;
        
          this.router.navigate(['/reset-password'], { queryParams: { email: this.forgetpassForm.value.email } });
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = 'Email not found or something went wrong.';
        }
      });
    }

  }
}
