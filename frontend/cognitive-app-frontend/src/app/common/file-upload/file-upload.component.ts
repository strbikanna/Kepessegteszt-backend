import {Component, Input} from '@angular/core';
import {HttpClient, HttpEventType} from "@angular/common/http";
import {Observable, Subscription} from "rxjs";

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss']
})
export class FileUploadComponent {
  @Input({required: true}) onUpload!: (data: FormData) => Observable<any>;
  @Input() fileType: string = 'image/*';
  fileName = '';
  uploadProgress: number | null = null;
  uploadSub: Subscription | null = null;

  onFileSelected(event: any) {

    const file: File = event.target.files[0];

    if (file) {
      this.fileName = file.name;
      const formData = new FormData();
      formData.append("thumbnail", file);

      this. uploadSub = this.onUpload(formData).subscribe(event => {
        if (event.type !== undefined && event.type == HttpEventType.UploadProgress) {
          this.uploadProgress = Math.round(100 * (event.loaded / event.total));
        }
      })
    }
  }

  cancelUpload() {
    this.uploadSub?.unsubscribe();
    this.reset();
  }

  reset() {
    this.uploadProgress = null;
    this.uploadSub = null;
  }
}
