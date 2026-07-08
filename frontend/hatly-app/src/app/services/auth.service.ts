import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable,tap} from 'rxjs';

interface LoginRequest{
    email:string;
    password:string;
}
export interface AuthResponse {
  message: string;
  accessToken: string;
  refreshToken: string;
  user: UserData;
}

export interface UserData {
  id: number;
  email: string;
  phone: string;
  systemRole: string;
}
export interface ResetPass{
  otp:string,
  email:string,
  newPassword:string
}


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  


  private baseUrl = 'http://localhost:8080/account'; 

  constructor(private http: HttpClient) { }

  register(payload: any): Observable<any> {
  
    return this.http.post(`${this.baseUrl}/register`, payload);
}

login(payload:LoginRequest) : Observable<AuthResponse>{

  return this.http.post<AuthResponse>(`${this.baseUrl}/login`,payload).pipe(
      tap((response=>
      {
        if(response && response.accessToken)
        {
          localStorage.setItem('accesstoken',response.accessToken),
          localStorage.setItem('refreshtoken',response.refreshToken),
          localStorage.setItem('currentuser',JSON.stringify(response.user))

        }
      })
      )
 )}

 getCurrentUserId(): number | null {
  const userJson = localStorage.getItem('currentuser');
  
  if (userJson) {
    const user = JSON.parse(userJson); 
    return user.id; 
  }
  
  return null;
}
getCurrentUsername(): string | null {
const userJson = localStorage.getItem('currentuser');
  if (userJson) {
    const data = JSON.parse(userJson);

  
    const email = data.email; 
    
    if (email) {
      const rawName = email.split('.')[0]; 
      return rawName.charAt(0).toUpperCase() + rawName.slice(1); 
    }
  }
  return null;
}

isLoggedIn(): boolean {
    return !!localStorage.getItem('accessToken');
  }


  logout(): void {
    localStorage.clear();
  }

  forgotpassword(payload:{email:string}):Observable<any>
  {
    return this.http.post(`${this.baseUrl}/forget-password`,payload)
  }
  resetpassword(payload:ResetPass):Observable<any>
  {
    return this.http.post(`${this.baseUrl}/reset-password`,payload)
  }
}