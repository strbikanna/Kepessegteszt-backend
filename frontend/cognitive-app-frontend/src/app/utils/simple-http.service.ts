import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AppConstants} from "./constants";

@Injectable({
  providedIn: 'root'
})
export class SimpleHttpService {
  protected readonly baseUrl = AppConstants.resourceServerUrl;

  constructor(protected http: HttpClient) { }
  handleHttpError(error: any) {

  }
}
