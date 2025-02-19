import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {
    AbstractControl,
    ControlValueAccessor,
    FormBuilder,
    ValidationErrors,
    ValidatorFn,
    Validators
} from "@angular/forms";
import {ConfigItem} from "../../../../model/config_item.model";
import {TEXTS} from "../../../../text/app.text_messages";

@Component({
    selector: 'app-config-item-form',
    templateUrl: './config-item-form.component.html',
    styleUrls: ['./config-item-form.component.scss']
})
export class ConfigItemFormComponent implements ControlValueAccessor, OnInit{

    @Input() configItemData: ConfigItem | undefined;
    @Input() initialEditable: boolean = true;
    @Input() forbiddenParamOrders: number[] = [];
    @Output() delete = new EventEmitter<void>();
    @Output() save = new EventEmitter<ConfigItem>();

    private onResult: (configItem: ConfigItem) => void = () => {};

    protected errorMessages = TEXTS.error
    protected texts = TEXTS.game_management.config_item_form;
    protected actionTexts = TEXTS.actions
    protected editable = true
    protected configItem : ConfigItem | undefined;
    protected minParamOrder: number = 1;

    private areValuesValid: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
        const configItem = this.getConfigItemFromForm(control);
        const validConfig = (configItem.easiestValue < configItem.hardestValue && configItem.increment > 0
                && configItem.initialValue >= configItem.easiestValue && configItem.initialValue <= configItem.hardestValue) ||
            (configItem.easiestValue > configItem.hardestValue && configItem.increment < 0
                && configItem.initialValue <= configItem.easiestValue && configItem.initialValue >= configItem.hardestValue) ||
            (configItem.easiestValue === configItem.hardestValue && configItem.initialValue === configItem.easiestValue
                && configItem.increment === 0);
        return validConfig ? null : {invalidValues: true};
    }
    private paramOrderValid: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
        const paramOrder = control.value
        if(this.configItem?.paramOrder === paramOrder) return null;
        return this.forbiddenParamOrders.includes(paramOrder) ? {invalidParamOrder: true} : null;
    }

    protected configItemForm = this.fb.group({
        paramName: ['', Validators.required],
        initialValue: [0, Validators.required],
        hardestValue: [10, Validators.required],
        easiestValue: [1, Validators.required],
        increment: [1, Validators.required],
        paramOrder: [this.minParamOrder, [Validators.required, Validators.min(this.minParamOrder), this.paramOrderValid]],
        description: ['']
    }, {validators: this.areValuesValid})

    constructor(private fb: FormBuilder) {}

    ngOnInit(): void {
        this.editable = this.initialEditable;
        if (this.configItemData) {
            this.writeValue(this.configItemData);
        }else{
            for(let i = 1; i< this.forbiddenParamOrders.length+2; i++){
                if(!this.forbiddenParamOrders.includes(i)){
                    this.minParamOrder = i;
                    break;
                }
            }
        }
    }

    writeValue(configItem: ConfigItem): void {
        this.configItem = configItem;
        if (this.configItem) {
            this.setFormValues(configItem);
            this.minParamOrder = this.configItem.paramOrder;
        }
    }
    registerOnChange(fn: any): void {
        this.onResult = fn;
    }
    registerOnTouched(fn: any): void { }
    setDisabledState?(isDisabled: boolean): void {
        this.editable = !isDisabled;
    }

    protected onSubmit() {
        const configItem: ConfigItem = this.getConfigItemFromForm(this.configItemForm);
        configItem.id = this.configItem?.id;
        this.configItem = configItem;
        this.editable = false;
        this.onResult(configItem);
        this.save.emit(configItem);
    }
    protected onCancel() {
        this.editable = false;
        if(this.configItemForm.invalid && this.configItem){
            this.setFormValues(this.configItem);
        }
        if(this.configItemForm.invalid){
            this.delete.emit();
        }
    }
    protected onEdit() {
        this.editable = true;
    }
    protected onDelete() {
        this.delete.emit();
    }

    private getConfigItemFromForm(control: AbstractControl): ConfigItem {
        return {
            id: undefined,
            paramName: control.get('paramName')?.value!!,
            initialValue: control.get('initialValue')?.value!!,
            hardestValue: control.get('hardestValue')?.value!!,
            easiestValue: control.get('easiestValue')?.value!!,
            increment: control.get('increment')?.value!!,
            paramOrder: control.get('paramOrder')?.value!!,
            description: control.get('description')?.value ?? ''
        };
    }

    private setFormValues(configItem: ConfigItem) {
        this.configItemForm.controls.paramName.setValue(configItem.paramName);
        this.configItemForm.controls.initialValue.setValue(configItem.initialValue);
        this.configItemForm.controls.hardestValue.setValue(configItem.hardestValue);
        this.configItemForm.controls.easiestValue.setValue(configItem.easiestValue);
        this.configItemForm.controls.increment.setValue(configItem.increment);
        this.configItemForm.controls.paramOrder.setValue(configItem.paramOrder);
        this.configItemForm.controls.description.setValue(configItem.description);
    }

}
