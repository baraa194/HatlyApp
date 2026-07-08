import { Component ,OnInit} from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { Router,RouterLink} from '@angular/router';
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule,RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit{

loginForm!:FormGroup
errormsg:string=''

  constructor(private authService: AuthService,private router: Router) { 
  }

  ngOnInit(): void {
      this.loginForm=new FormGroup({
       email:new FormControl('',[Validators.required,Validators.email]),
        password:new FormControl('',[Validators.required,Validators.minLength(8)])


      })      
  
  }

  onSubmit():void{
    if(this.loginForm.valid)
    {
      this.authService.login(this.loginForm.value).subscribe({
        next:(response)=>{
      const userRole = response.user.systemRole.toUpperCase();
          if(userRole==='DELIVERY_AGENT')
          {
                this.router.navigate(['/delivery'])
          }
          else{
              this.router.navigate(['/home'])
          }
        
        
        },
        error: (err) => {
          console.error('Login error:', err);
          this.errormsg = 'Invalid email or password. Please try again.';
        }
      })
    }
  }



}
