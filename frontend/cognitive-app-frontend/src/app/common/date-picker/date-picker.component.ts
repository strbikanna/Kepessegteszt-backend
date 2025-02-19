import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TEXTS} from "../../text/app.text_messages";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
    selector: 'app-date-picker',
    templateUrl: './date-picker.component.html',
    styleUrls: ['./date-picker.component.scss']
})
export class DatePickerComponent {
    text = TEXTS.date_picker
    errorText = TEXTS.error
    @Input() title?: string
    @Input() subtitle?: string
    @Input() buttonText?: string

    dateForm = new FormGroup({
        start: new FormControl<Date | null>(null),
        end: new FormControl<Date | null>(null),
    })
    @Output() dateChosen = new EventEmitter<DateRange>

    onDateChosen() {
        if (this.dateForm.controls.start.value == null || this.dateForm.controls.end.value == null) {
            return
        }
        this.dateChosen.emit({
            start: this.dateForm.controls.start.value,
            end: this.dateForm.controls.end.value
        })
    }
}

export interface DateRange {
    start: Date
    end: Date
}
