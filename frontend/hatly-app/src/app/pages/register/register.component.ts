import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss' 
})
export class RegisterComponent implements OnInit {
  
  registerForm!: FormGroup;

  constructor(private authService: AuthService) {
  
  }

  ngOnInit(): void {
  
    this.registerForm = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.minLength(3)]),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
      phone: new FormControl('', [Validators.required, Validators.pattern('^01[0125][0-9]{8}$')]),
      role: new FormControl('CUSTOMER', [Validators.required])
    });

  
    this.registerForm.get('role')?.valueChanges.subscribe((selectedRole) => {
      this.handleRoleChanges(selectedRole);
    });
  } 


  private handleRoleChanges(role: string): void {
    if (role === 'RESTAURANT_USER') {
      this.registerForm.addControl('restaurantName', new FormControl('', [Validators.required]));
      this.registerForm.addControl('logoUrl', new FormControl('', [Validators.required]));
    } else {
      this.registerForm.removeControl('restaurantName');
      this.registerForm.removeControl('logoUrl');
    }
    this.registerForm.updateValueAndValidity();
  }


  onSubmit(): void {
    if (this.registerForm.valid) {
      const formValue = this.registerForm.value;
      
      const backendPayload = {
        name: formValue.name,
        email: formValue.email,
        password: formValue.password,
        phone: formValue.phone,
        role: formValue.role,
        restaurant: formValue.role === 'RESTAURANT_USER' ? {
          name: formValue.restaurantName,
          logoUrl: formValue.logoUrl
        } : null
      };
      this.authService.register(backendPayload).subscribe({
        next: (response) => {
          console.log('Success! Account created in backend:', response);
          alert('Account created successfully!');
  
        },
        error: (err) => {
          console.error('Error from backend:', err);
          alert('Registration failed. Please try again.');
        }
      });

      console.log('Payload:', backendPayload);
    }
  }

} 