## 기존 프로젝트에 Compose 통합 연습 해보는 곳

## [요약]

- 기존 activity, fragment, xml, 커스텀 뷰 등에, 컴포즈 요소를 더하기 가능
- ViewModel, Navigation, Hilt, Paging 등의 다른 라이브러리와도 함께 사용 가능
- 아주 간단한 샘플코드
    
    [https://github.com/nosorae/ComposeIntegration](https://github.com/nosorae/ComposeIntegration)
    

## [**도입 시 고려요소]**

- apk volume
    - 라이브러리 추가하면 당연히 용량 업
    - 하지만 전부 마이그레이션 한다고 가정했을 때는 다운 예상
    - 마이그레이션 중에는 업
- build time
    - 전부 마이그레이션 시 29% 향상되었다고 합니다.
    - 마이그레이션 중에는 7.6% 느려졌다고 합니다.
- runtime performance
    - 기존 View system 에서 얻은 교훈을 통해 퍼포먼스 더 올렸다고 합니다..
        - 효율적인 recompose (업데이트가 필요한 부분만 다시 그린다는 뜻??)
        - 기존 multiple layout pass → Compose 는 single layout pass
        - 기존 runtime inflate → Compose 는 compile time 에 쓰여짐
        - Macrobenchmark 로 Compose 벤치마크 가능하다고 합니다.

**→ 결론 : 도입하고 마이그레이션 중인 상태를 오래 끌기보다 빠르게 완전히 마이그레이션하는 편이 앱 용량과 성능및 코드관리 측면에서 좋을 듯 합니다.**

## [**기존 View system 과 얼마나 잘 통합될까?]**

- 기존 View 와 Compose 는 상호 통합 가능
    - **기존 activity, fragment, view layout, 커스텀 뷰 등에, 컴포즈 요소를 더하기 가능 (예시1 참고)**
    - composable function 에 → 기존 view-based UI 요소 더하기 가능
- **ViewModel, Navigation, Hilt, Paging 등의 다른 라이브러리와도 함께 사용 가능 
(ViewModel, LiveData 는 예시1 깃헙 참고)**
- 마이그레이션 방식
    - bottom-up : 작은 뷰 요소들(ex. TextView, Button ...)부터 변경하여 container 로 넓혀나가며 모든 요소 변경하는 방식 (아래 예시1 참고)
    - top-down : container 부터 바꾸고 작은 뷰 요소들도 변경하여 모든 요소 변경하는 방식 (Activity setContentView(R.layout.~) 걷어내고 setContent 에 Composable 넣는 방식
- 기존 스플 프로젝트에 컴포즈 개발환경 셋업 (현재 프로젝트에서 고려해봐야하거나 추가할 것만 정리)
    - **kotlin version 1.6.10 설정 확인!
    (빌드 이슈, 처음부터 컴포즈 프로젝트로 만들 때는 문제 없었는데 일반 빈 프로젝트로 생성 시)**
    - app module - android
        - 추가
            
            ```
            buildFeatures {
                    // Enables Jetpack Compose for this module
                    compose true
                }
            ```
            
            ```
            composeOptions {
                    kotlinCompilerExtensionVersion '1.1.1'
                }
            ```
            
    - app module - dependencies
        - 추가
            
            ```kotlin
                // Integration with activities 
            		// (ex - ComponentActivity.setContent)
                implementation 'androidx.activity:activity-compose:1.4.0'
                // Compose Material Design
                implementation 'androidx.compose.material:material:1.1.1'
                // Animations
                implementation 'androidx.compose.animation:animation:1.1.1'
                // Tooling support (Previews, etc.)
                implementation 'androidx.compose.ui:ui-tooling:1.1.1'
                // Integration with ViewModels
                implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1'
                // UI Tests
                androidTestImplementation 'androidx.compose.ui:ui-test-junit4:1.1.1'
            ```
            
- 예시1 - xml 에 Composable 함수로  텍스트 뷰 띄우기
    - <`androidx.compose.ui.platform.ComposeView`> 태그 활용 (뷰의 위치는 기존 xml 에서 정한 것과같이 정할 수도 있음.)
    - 이전과 똑같이 id 로 ComposeView 를 찾아와서, 액티비티 전체를 컴포즈로 만들때와같이 ComposeView 의 setContent 람다에 Compose 함수를 호출하면 됨
    - bottom-up 방식은 이 방법이 편할 것으로 예상됨.
    - setContentView(R.layout.~) 와 setContent 를 같이사용할 수 있나요? → 없음, onCreate 에서 나중에 작성된 것으로 화면이 나옴
- 예시2 - RecyclerView 의 아이템만을 Compose 로 갈아끼우기 (작성 대기)
    - 참고 소스코드
        
        ```kotlin
        package com.google.samples.apps.sunflower.adapters
        
        import androidx.compose.runtime.Composable
        import androidx.compose.ui.platform.ComposeView
        import androidx.compose.ui.platform.ViewCompositionStrategy
        import androidx.recyclerview.widget.DiffUtil
        import androidx.recyclerview.widget.ListAdapter
        import androidx.recyclerview.widget.RecyclerView
        
        abstract class ComposeListAdapter<T, VH : ComposeViewHolder<T>>(
            diffCallback: DiffUtil.ItemCallback<T>
        ) : ListAdapter<T, VH>(diffCallback) {
        
            override fun onViewRecycled(holder: VH) {
                holder.composeView.disposeComposition()
                super.onViewRecycled(holder)
            }
        }
        
        abstract class ComposeRecyclerViewAdapter<VH : ComposeViewHolder<*>> : RecyclerView.Adapter<VH>() {
        
            override fun onViewRecycled(holder: VH) {
                holder.composeView.disposeComposition()
                super.onViewRecycled(holder)
            }
        }
        
        abstract class ComposeViewHolder<T>(
            val composeView: ComposeView
        ) : RecyclerView.ViewHolder(composeView) {
        
            @Composable
            abstract fun ViewHolder(input: T)
        
            init {
                composeView.setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )
            }
        
            fun bindViewHolder(input: T) {
                composeView.setContent {
                    ViewHolder(input)
                }
            }
        }
        
        ```
        
- 예시3 - 스플의 뷰 하나 Copmose 로 갈아끼워보기 (작성 대기)

## [Composable function 에 대한 이해]

- 함수 상단에 @Composable 붙여서 만들 수 있음
- Composable 함수는 오직 또 다른 Composable 함수에서만 호출될 수 있음
    - setContent 도 Composable 함수였던 것!
    - androidx.compose.material.~ 에 있는 컴포넌트(?)들(ex. Text)도 다 Composable 함수 였던 것!
- 멱등성을 가지게 하는 것이 좋음. 그러므로
    - 인자로 전달된 데이터로 뷰를 업데이트 하는 방식 o
    - 전역변수 x
    - random 값 x
- Composable 함수 안에서 다른 Compose 함수를 호출하는 것이 Compose 가 UI hierarchy 를 만드는 방식 (예를 들어 아래 코드와 같은 느낌)
    
    ```kotlin
    Row {
            Image(
                painter = painterResource(R.drawable.profile_picture),
                contentDescription = "Contact profile picture",
            )
    
           Column {
                Text(text = msg.author)
                Text(text = msg.body)
            }
    
        }
    ```
    
    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/97e1dac2-6262-436e-a34d-d1c5bc24e090/Untitled.png)
