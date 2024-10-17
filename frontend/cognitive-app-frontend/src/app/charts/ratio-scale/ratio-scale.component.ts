import {
    AfterViewInit,
    ChangeDetectorRef,
    Component,
    ElementRef,
    Input,
    OnChanges,
    OnInit,
    SimpleChanges,
    ViewChild
} from '@angular/core';

@Component({
    selector: 'app-ratio-scale',
    templateUrl: './ratio-scale.component.html',
    styleUrls: ['./ratio-scale.component.scss']
})
export class RatioScaleComponent implements AfterViewInit, OnChanges {

    @Input({required:true}) ownValue!: number
    @Input({required:true}) groupValue!: number
    @Input({required:true}) wholeValue!: number


    @ViewChild('chipOwn') chipOwn?: ElementRef;
    @ViewChild('scaleOwn') scaleOwn?: ElementRef;
    @ViewChild('chipGroup') chipGroup?: ElementRef;
    @ViewChild('scaleGroup') scaleGroup?: ElementRef;


    ngAfterViewInit(): void {
        this.initRatios()
    }
    ngOnChanges(changes: SimpleChanges): void {
        this.initRatios();
    }

    initRatios(){
        if(
            this.ownValue && this.groupValue && this.wholeValue
            && this.scaleOwn && this.scaleGroup && this.chipOwn && this.chipGroup
        ) {
            const ratioOwn = (this.ownValue / this.wholeValue) * 100
            const ratioGroup = (this.groupValue / this.wholeValue) * 100

            this.scaleOwn!.nativeElement.style.width = ratioOwn + '%'
            this.scaleGroup!.nativeElement.style.width = ratioGroup + '%'
            if(ratioOwn > ratioGroup){
                this.scaleOwn!.nativeElement.style.zIndex = '1'
                this.scaleGroup!.nativeElement.style.zIndex = '2'
            }else{
                this.scaleOwn!.nativeElement.style.zIndex = '2'
                this.scaleGroup!.nativeElement.style.zIndex = '1'
            }

            this.chipOwn!.nativeElement.style.left = 'calc(' + ratioOwn + '% - 1em)'
            this.chipGroup!.nativeElement.style.left = 'calc(' + ratioGroup + '% - 3.4em)'
        }

    }
    roundDataValues(data: number){
        return Math.round(data * 100) / 100
    }

}
