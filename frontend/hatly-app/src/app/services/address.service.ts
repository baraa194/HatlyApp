import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CustomerAddress{
      id:number,
     label:string,
     street:string,
     city:string,
     apartmentNumber:string,
    building:string,
     type:string,
     lat:number,
     lng:number,
    isDefault:boolean
}



@Injectable({
  providedIn: 'root'
})

export class AddressService{

private baseUrl = 'http://localhost:8080/addresses';
constructor(private http:HttpClient){}

getAllAddresses(userId:number) :Observable<CustomerAddress[]>
{
    return this.http.get<CustomerAddress[]>(`${this.baseUrl}?userId=${userId}`)
}
getDefaultAddress(userId: number): Observable<CustomerAddress> {
    return this.http.get<CustomerAddress>(`${this.baseUrl}/user/${userId}/default`);
  }





}