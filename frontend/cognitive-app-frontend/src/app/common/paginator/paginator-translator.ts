import {Subject} from "rxjs";
import {MatPaginatorIntl} from "@angular/material/paginator";
import {Injectable} from "@angular/core";
import {TEXTS} from "../../text/app.text_messages";

/**
 * This class is used to translate the paginator labels
 */
@Injectable(
    {providedIn: 'root'}
)
export class PaginatorTranslator implements MatPaginatorIntl {

    private text = TEXTS.paging;
    changes = new Subject<void>();


    firstPageLabel = this.text.firstPage;
    itemsPerPageLabel = this.text.itemsPerPage;
    lastPageLabel = this.text.lastPage;
    nextPageLabel = this.text.nextPage;
    previousPageLabel = this.text.previousPage;

    getRangeLabel(page: number, pageSize: number, length: number): string {
        if (length === 0) {
            return '1 / 1';
        }
        const amountPages = Math.ceil(length / pageSize);
        return `${page + 1} / ${amountPages}`;
    }
}