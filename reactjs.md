- [核心概念](#核心概念)
    * [元素渲染](#元素渲染)
    * [组件&Props](#组件&Props)

---
* Refs:
    * > https://zh-hans.reactjs.org/docs/introducing-jsx.html
---

## 核心概念
### 元素渲染
* 将一个元素渲染为DOM
    ```jsx
    const element = <h1>Hello, world</h1>;
    ReactDOM.render(element, document.getElementById('root'));
    ```
    * 有一个叫“element”的节点，该节点内的所有内容都将由React DOM管理。
    * 想要将一个React元素渲染到根DOM节点中，只需把它们一起传入`ReactDOM.render()`。
    * 仅使用React构建的应用通常只有单一的根DOM节点，可以将多个DOM节点放进一个根节点中，再把这个根节点传入React。
* 更新已渲染的元素
    * React元素是不可变对象。一旦被创建，你就无法更改它的子元素或者属性。
    * 根据我们已有的知识，更新UI唯一的方式是创建一个全新的元素，并将其传入`ReactDOM.render()`。
    * 一个计时器的例子: 
        ```jsx
        function tick() {
            const element = (
                <div>
                    <h1>Hello, world!</h1>
                    <h2>It is {new Date().toLocaleTimeString()}.</h2>
                </div>
            );
            ReactDOM.render(element, document.getElementById('root'));
        }
        setInterval(tick, 1000);
        ```
        * 这个例子会在`setInterval()`回调函数，每秒都调用`ReactDOM.render()`。
* React只更新它需要更新的部分
    * React DOM会将元素和它的子元素与它们之前的状态进行比较，并只会进行必要的更新来使DOM达到预期的状态。

### 组件&Props
* 组件，从概念上类似于JavaScript函数。它接受任意的入参(即 “props”)，并返回用于描述页面展示内容的React元素。
* 函数组件与class组件
    * 定义组件最简单的方式就是编写JavaScript函数：
        ```jsx
        function Welcome(props) {
            return <h1>Hello, {props.name}</h1>;
        }
        ```
        * 该函数是一个有效的React组件，因为它接收唯一带有数据的“props”(代表属性)对象与并返回一个React元素。
        * 这类组件被称为“函数组件”，因为它本质上就是JavaScript函数。
        * 你同时还可以使用ES6的class来定义组件:
        ```jsx
        class Welcome extends React.Component {
            render() {
                return <h1>Hello, {this.props.name}</h1>;
            }
        }
        ```
        * 上述两个组件在React里是等效的。
* 渲染组件
    * React元素也可以是用户自定义的组件
    * 当React元素为用户自定义组件时，它会将JSX所接收的属性以及子组件转换为单个对象传递给组件，这个对象被称之为“props”。
        ```jsx
        function Welcome(props) {
            return <h1>Hello, {props.name}</h1>;
        }
        const element = <Welcome name="Sara" />;
        ReactDOM.render(element, document.getElementById('root'));
        ```
        1. 我们调用`ReactDOM.render()`函数，并传入`<Welcome name="Sara" />`作为参数。
        2. React调用Welcome组件，并将`{name: 'Sara'}`作为props传入。
        3. Welcome组件将`<h1>Hello, Sara</h1>`元素作为返回值。
        4. React DOM将DOM高效地更新为`<h1>Hello, Sara</h1>`。
* 组合组件
    * 组件可以在其输出中引用其他组件。这就可以让我们用同一组件来抽象出任意层次的细节。
        ```jsx
        function Welcome(props) {
            return <h1>Hello, {props.name}</h1>;
        }
        function App() {
            return (
                <div>
                    <Welcome name="Sara" />
                    <Welcome name="Cahal" />
                    <Welcome name="Edite" />
                </div>
            );
        }
        ReactDOM.render(
            <App />,
            document.getElementById('root')
        );
        ```
* 提取组件
    * 将组件拆分为更小的组件。
        ```jsx
        function Comment(props) {
            return (
                <div className="Comment">
                    <div className="UserInfo">
                        <img className="Avatar" src={props.author.avatarUrl} alt={props.author.name} />
                        <div className="UserInfo-name">
                            {props.author.name}
                        </div>
                    </div>
                    <div className="Comment-text">
                        {props.text}
                    </div>
                    <div className="Comment-date">
                        {formatDate(props.date)}
                    </div>
                </div>
            );
        }
        ```
        * 该组件用于描述一个社交媒体网站上的评论功能，它接收author(对象)，text(字符串)以及date(日期)作为props。
        * 该组件由于嵌套的关系，变得难以维护，且很难复用它的各个部分。因此，让我们从中提取一些组件出来。
        ```jsx
        function formatDate(date) {
            return date.toLocaleDateString();
        }
        function Avatar(props) {
            return (
                <img className="Avatar" src={props.user.avatarUrl} alt={props.user.name} />
            );
        }
        function UserInfo(props) {
            return (
                <div className="UserInfo">
                    <Avatar user={props.user} />
                    <div className="UserInfo-name">{props.user.name}</div>
                </div>
            );
        }
        function Comment(props) {
            return (
                <div className="Comment">
                <UserInfo user={props.author} />
                <div className="Comment-text">{props.text}</div>
                <div className="Comment-date">
                    {formatDate(props.date)}
                </div>
                </div>
            );
        }
        const comment = {
            date: new Date(),
            text: 'I hope you enjoy learning React!',
            author: {
                name: 'Hello Kitty',
                avatarUrl: 'https://placekitten.com/g/64/64',
            },
        };
        ReactDOM.render(
            <Comment date={comment.date} text={comment.text} author={comment.author} />,
            document.getElementById('root')
        );
        ```