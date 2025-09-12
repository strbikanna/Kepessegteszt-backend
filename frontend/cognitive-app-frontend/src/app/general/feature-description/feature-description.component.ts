import {Component, OnInit} from '@angular/core';
import {TEXTS} from "../../text/app.text_messages";

@Component({
  selector: 'app-feature-description',
  templateUrl: './feature-description.component.html',
  styleUrls: ['./feature-description.component.scss']
})
export class FeatureDescriptionComponent implements  OnInit{

  actualFeature = TEXTS.home.featureList[0]
  featureList = TEXTS.home.featureList
  protected actualFeatureIndex = 0
  text = TEXTS.home

  ngOnInit() {
    setInterval(()=>{
      this.rotateFeature()
    }, 4000)
  }

  rotateFeature(){
    if(this.actualFeatureIndex == this.featureList.length-1){
      this.actualFeatureIndex = 0
    }else{
      this.actualFeatureIndex++
    }

    this.actualFeature = this.featureList[this.actualFeatureIndex]
  }

}
