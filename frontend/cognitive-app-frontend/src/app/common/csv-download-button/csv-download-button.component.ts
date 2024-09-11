import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TEXTS} from "../../utils/app.text_messages";
import {map, Observable, tap} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {SimpleHttpService} from "../../utils/simple-http.service";

@Component({
    selector: 'app-csv-download-button',
    templateUrl: './csv-download-button.component.html',
    styleUrls: ['./csv-download-button.component.scss']
})
export class CsvDownloadButtonComponent {
    protected tooltipText = TEXTS.csv_download.download_info;
    @Input({required: true}) csvLink!: string
    @Output() click = new EventEmitter<void>();

    constructor(private http: HttpClient) {}

    onClick() {
        this.click.emit();
        this.downloadCsvLink()
    }

    downloadCsvLink(){
        this.http.get(this.csvLink, {responseType: "text"}).subscribe(data =>
             {
                const blob = new Blob([data]);
                let url = window.URL.createObjectURL(blob);
                 let link = document.createElement('a');
                 link.href = url;
                 link.setAttribute('download', 'test_res.csv');
                 document.body.appendChild(link);
                 link.click();
            }
        );
    }
}
