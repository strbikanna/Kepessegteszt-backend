import {Component, ElementRef, HostListener, Input} from '@angular/core';
import {NG_VALUE_ACCESSOR} from "@angular/forms";
import {TEXTS} from "../../text/app.text_messages";

/**
 * File upload form component
 */
@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: FileUploadComponent,
      multi: true
    }
  ]
})
export class FileUploadComponent {
  @Input() fileType: string = 'image/*';
  onChange!: Function;
  private file: File | null = null;
  fileName : string | undefined;
  text = TEXTS.file_upload.no_content

  @HostListener('change', ['$event.target.files']) emitFiles( event: FileList ) {
    const file = event && event.item(0);
    this.onChange(file);
    this.file = file;
  }

  constructor( private host: ElementRef<HTMLInputElement> ) {
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.fileName = file.name;
    }
  }
  writeValue( value: null ) {
    this.host.nativeElement.value = '';
    this.file = null;
    this.fileName = undefined;
  }

  registerOnChange( fn: Function ) {
    this.onChange = fn;
  }

  registerOnTouched( fn: Function ) {
  }

}
