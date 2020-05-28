## EventEmitter

EventEmitter는 말 그대로 `이벤트를 발산 혹은 방출한다는 뜻입니다.` 이를 호출하다라고 이해하면 혼란이 오기 때문에 이벤트 호출과는 다른 개념입니다.

class 개요
- EventEmitter의 기본적인 구성은 아래 코드와 같습니다.

```javascript
class EventEMITTER {
    constructor(isAsync?: boolean)
    emit(value?: T)
    subscribe(generatorOrNext?: any, error?: any, complete?: any): any
}
```

EventEmitter를 활용한 버튼 토글 이벤트(Button Toggle Event) 예제를 살펴보겠습니다.

다음 코드에서 `Toggle`이라는 메시지가 담긴 영역을 클릭하면 `zippy` 클래스는 open과 close라는 두 가지 이벤트를 각 상황에 맞게 방출(emit)합니다.

```javascript
@Component({
    selector: 'zippy',
    template: `
    <div class="zippy">
        <div (click)="toggle()">Toggle</div>
        <div [hidden]="!visible">
            <ng-content></ng-content>
        </div>
    </div>`
})

export class Zippy {
    visivle: boolean = true;
    @Output() open: EventEmitter = new EventEmitter();
    @Output() close: EventEmitter = new EventEmitter();
    public toggle() {
        this.visible = !this.visible;
        if (this.visible) {
            this.open.emit(null);
        } else {
            this.close.emit(null);
        }
    }
}
```
