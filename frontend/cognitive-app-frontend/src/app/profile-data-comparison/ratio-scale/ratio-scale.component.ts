import {AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {Observable} from "rxjs";

@Component({
    selector: 'app-ratio-scale',
    templateUrl: './ratio-scale.component.html',
    styleUrls: ['./ratio-scale.component.scss']
})
export class RatioScaleComponent implements OnInit, AfterViewInit {

    @Input({required:true}) ownValue!: Observable<number>
    @Input({required:true}) groupValue!: Observable<number>
    @Input({required:true}) wholeValue!: Observable<number>

    protected dataOwn: number | undefined;
    protected dataGroup: number | undefined;
    private dataWhole: number | undefined;

    @ViewChild('chipOwn') chipOwn?: ElementRef;
    @ViewChild('scaleOwn') scaleOwn?: ElementRef;
    @ViewChild('chipGroup') chipGroup?: ElementRef;
    @ViewChild('scaleGroup') scaleGroup?: ElementRef;

    ngOnInit() {
        this.ownValue.subscribe(value => {
            this.dataOwn = value

            this.groupValue.subscribe(value => {
                this.dataGroup = value

                this.wholeValue.subscribe(value => {
                    this.dataWhole = value
                })
            })
        })
    }

    ngAfterViewInit(): void {
        this.initRatios()
    }

    initRatios(){
        if(
            this.dataOwn && this.dataGroup && this.dataWhole
            && this.scaleOwn && this.scaleGroup && this.chipOwn && this.chipGroup
        ) {
            console.log("setting ratios")
            const ratioOwn = (this.dataOwn / this.dataWhole) * 100
            const ratioGroup = (this.dataGroup / this.dataWhole) * 100

            this.scaleOwn!.nativeElement.style.width = ratioOwn + '%'
            this.scaleGroup!.nativeElement.style.width = ratioGroup + '%'
            if(ratioOwn > ratioGroup){
                this.scaleOwn!.nativeElement.style.zIndex = '1'
                this.scaleGroup!.nativeElement.style.zIndex = '2'
            }else{
                this.scaleOwn!.nativeElement.style.zIndex = '2'
                this.scaleGroup!.nativeElement.style.zIndex = '1'
            }

            this.chipOwn!.nativeElement.style.left = 'calc(' + ratioOwn + '% - 0.8em)'
            this.chipGroup!.nativeElement.style.left = 'calc(' + ratioGroup + '% - 3em)'
        }

    }
}
